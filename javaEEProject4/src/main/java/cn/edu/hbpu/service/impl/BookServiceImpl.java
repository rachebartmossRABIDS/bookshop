package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.exception.BusinessException;
import cn.edu.hbpu.mapper.BookMapper;
import cn.edu.hbpu.mapper.KindMapper;
import cn.edu.hbpu.mapper.ShopMapper;
import cn.edu.hbpu.mapper.SearchLogMapper;
import cn.edu.hbpu.pojo.Book;
import cn.edu.hbpu.pojo.Kind;
import cn.edu.hbpu.service.BookService;
import cn.edu.hbpu.service.OperationLogService;
import cn.edu.hbpu.service.CacheService;
import cn.edu.hbpu.utils.UserContext;
import cn.edu.hbpu.utils.ValidateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final KindMapper kindMapper;
    private final ShopMapper shopMapper;
    private final SearchLogMapper searchLogMapper;
    private final OperationLogService operationLogService;
    private final CacheService cacheService;

    @Override
    public List<Book> listByKid(Integer kid){
        return bookMapper.selectBykid(kid);
    }

    @Override
    public List<Book> listBySid(Integer sid) {
        return bookMapper.selectBySid(sid);
    }

    @Override
    public Book getByBid(Integer bid) {
        String key = "book:detail:" + bid;
        Book cached = cacheService.get(key, Book.class);
        if (cached != null) {
            return cached;
        }
        Book book = bookMapper.selectByPrimaryKey(Long.parseLong(bid+""));
        if (book != null) {
            cacheService.put(key, book, 300);
        }
        return book;
    }

    @Override
    public PageInfo<Book> selectByName(String name, String sort, Integer pageNo, Integer pageSize) {
        String keyword = name == null ? "" : name.trim();
        if (!keyword.isEmpty()) {
            searchLogMapper.insert(keyword);
        }
        //调用PageHelper执行分页
        PageHelper.startPage(pageNo,pageSize);//pageNo页码 pageSize每页显示的记录数
        List<Book> books = bookMapper.selectByName(keyword, sort);
        //查询结果封装成PageInfo对象
        PageInfo<Book> pageInfo = new PageInfo<>(books);
        return pageInfo;
    }

    @Override
    @Transactional
    public Boolean save(Book book) {
        requireAdmin();
        if (book == null || ValidateUtil.isBlank(book.getName()) || book.getName().trim().length() > 255) {
            throw new BusinessException("请填写图书名称（不超过 255 字）");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("请填写正确的图书价格");
        }
        if (book.getInventory() == null || book.getInventory() < 0) {
            book.setInventory(0);
        }
        if (book.getKid() == null) {
            throw new BusinessException("请选择所属分类");
        }
        Kind kind = kindMapper.selectByPrimaryKey(book.getKid().longValue());
        if (kind == null) {
            throw new BusinessException("所属分类不存在");
        }
        if (book.getSid() == null) {
            throw new BusinessException("请选择所属店铺");
        }
        if (shopMapper.selectByPrimaryKey(book.getSid().longValue()) == null) {
            throw new BusinessException("所属店铺不存在");
        }
        book.setName(book.getName().trim());
        // 冗余分类名，与现有数据保持一致
        book.setContent(kind.getContent());
        if (book.getState() == null || (book.getState() != 0 && book.getState() != 1)) {
            book.setState(0);
        }
        if (book.getBid() == null) {
            // 新增：销量从 0 开始，不允许客户端指定
            book.setVolume(0);
            boolean created = bookMapper.insertSelective(book) == 1;
            if (created) {
                operationLogService.log("新增图书", "图书 #" + book.getBid() + "：" + book.getName());
                cacheService.evict("book:detail:*");
            }
            return created;
        }
        if (bookMapper.selectByPrimaryKey(book.getBid().longValue()) == null) {
            throw new BusinessException("图书不存在");
        }
        // 编辑：销量由订单流程维护，不接受客户端修改
        book.setVolume(null);
        boolean updated = bookMapper.updateByPrimaryKeySelective(book) == 1;
        if (updated) {
            operationLogService.log("编辑图书", "图书 #" + book.getBid() + "：" + book.getName());
            cacheService.evict("book:detail:" + book.getBid());
        }
        return updated;
    }

    @Override
    @Transactional
    public int remove(Integer bid) {
        requireAdmin();
        if (bid == null || bookMapper.selectByPrimaryKey(bid.longValue()) == null) {
            throw new BusinessException("图书不存在");
        }
        try {
            int result = bookMapper.deleteByPrimaryKey(bid.longValue());
            if (result > 0) {
                operationLogService.log("删除图书", "图书 #" + bid);
                cacheService.evict("book:detail:" + bid);
            }
            return result;
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("该图书已有订单、评论或收藏记录，不能删除，可将其下架");
        }
    }

    private void requireAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException("需要管理员权限");
        }
    }
}
