package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Book;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BookService {
    public List<Book> listByKid(Integer kid);
    public List<Book> listBySid(Integer sid);
    public Book getByBid(Integer bid);
    PageInfo<Book> selectByName(String name, String sort, Integer pageNo, Integer pageSize);

    /** 管理员新增/编辑图书（bid 为空新增，否则更新） */
    Boolean save(Book book);

    /** 管理员删除图书（存在订单/评论/收藏引用时拒绝） */
    int remove(Integer bid);
}
