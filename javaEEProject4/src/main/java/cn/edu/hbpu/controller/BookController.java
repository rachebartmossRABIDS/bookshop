package cn.edu.hbpu.controller;

import cn.edu.hbpu.pojo.Book;
import cn.edu.hbpu.pojo.Result;
import cn.edu.hbpu.service.BookService;
import cn.edu.hbpu.service.UploadService;
import cn.edu.hbpu.utils.UserContext;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final UploadService uploadService;
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
    public Result selectByName(String name, String sort, Integer pageNo, Integer pageSize) {
        PageInfo<Book> pageInfo = bookService.selectByName(name, sort, pageNo, pageSize);
        return Result.success(pageInfo);
    }
    @PostMapping("/save")
    public Result save(Book book) {
        Boolean result = bookService.save(book);
        return result ? Result.success("保存成功", null) : Result.fail("保存图书失败");
    }

    @PostMapping("/remove")
    public Result remove(Integer bid) {
        int result = bookService.remove(bid);
        return result > 0 ? Result.success("删除成功", null) : Result.fail("删除图书失败");
    }

    @PostMapping("/uploadImage")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        if (!UserContext.isAdmin()) {
            return Result.fail("需要管理员权限");
        }
        String filename = uploadService.saveImage(file, "book");
        return Result.success(filename);
    }
}
