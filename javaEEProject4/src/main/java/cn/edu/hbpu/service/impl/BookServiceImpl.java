package cn.edu.hbpu.service.impl;

import cn.edu.hbpu.mapper.BookMapper;
import cn.edu.hbpu.pojo.Book;
import cn.edu.hbpu.service.BookService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
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
        return bookMapper.selectByPrimaryKey(Long.parseLong(bid+""));
    }

    @Override
    public PageInfo<Book> selectByName(String name, Integer pageNo, Integer pageSize) {
        //调用PageHelper执行分页
        PageHelper.startPage(pageNo,pageSize);//pageNo页码 pageSize每页显示的记录数
        List<Book> books = bookMapper.selectByName(name);
        //查询结果封装成PageInfo对象
        PageInfo<Book> pageInfo = new PageInfo<>(books);
        return pageInfo;
    }
}