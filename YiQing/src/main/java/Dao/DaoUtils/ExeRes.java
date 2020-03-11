package Dao.DaoUtils;

public class ExeRes {
    // 执行是否成功
    private Boolean exe_res;
    // 执行信息
    private String  exe_info;
    // 执行结果
    private Object exe_data;

    public Boolean getExe_res() {
        return exe_res;
    }

    public void setExe_res(Boolean exe_res) {
        this.exe_res = exe_res;
    }

    public String getExe_info() {
        return exe_info;
    }

    public void setExe_info(String exe_info) {
        this.exe_info = exe_info;
    }

    public Object getExe_data() {
        return exe_data;
    }

    public void setExe_data(Object exe_data) {
        this.exe_data = exe_data;
    }

    @Override
    public String toString(){
        String out = exe_res.toString() + "\t" + exe_info + "\t" ;
        out += exe_data;
        return out;
    }
}
