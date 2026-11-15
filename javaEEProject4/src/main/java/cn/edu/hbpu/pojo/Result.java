package cn.edu.hbpu.pojo;

public class Result {
    private Integer code;
    private String msg;
    private Object data;
    public Result(){}
    public Result(Integer code,String msg,Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static Result success(String msg,Object data){
        return new Result(1,msg,data);
    }
    public static Result success(Object data){
        return new Result(1,"success",data);
    }
    public static Result success(){
        return new Result(1,"success",null);
    }
    public static Result fail(String msg){
        return new Result(0,msg,null);
    }
    public static Result fail(){
        return new Result(0,"fail",null);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
