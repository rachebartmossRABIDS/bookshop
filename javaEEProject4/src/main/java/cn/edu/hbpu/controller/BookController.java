package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Book;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.BookService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    @GetMapping("/list/{kid}")
    public Result list(@PathVariable Integer kid){
        List<Book> books = bookService.listByKid(kid);
        return Result.success(books);
    }
    @GetMapping("/listBySid")
    public Result listBySid(Integer sid){
        return Result.success(bookService.listBySid(sid));
    }
    @GetMapping("/detail")
    public Result detail(Integer bid){
        Book book=bookService.getByBid(bid);
        return Result.success(book);
    }
    @PostMapping("/search")
    public Result selectByName(String name, Integer pageNo, Integer pageSize) {
        PageInfo<Book> pageInfo = bookService.selectByName(name, pageNo, pageSize);
        return Result.success(pageInfo);
    }
}
