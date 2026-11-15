package cn.edu.hbpu.service;

import cn.edu.hbpu.pojo.Book;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface BookService {
    public List<Book> listByKid(Integer kid);
    public List<Book> listBySid(Integer sid);
    public Book getByBid(Integer bid);
    PageInfo<Book> selectByName(String name,Integer pageNo, Integer pageSize);
}
