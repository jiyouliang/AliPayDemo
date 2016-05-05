package com.itheima.alipaydemo.model;

/**
 * 支付对象：服务器返回的json串
 * Created by youliang.ji on 2016/5/5.
 */
public class PrePayInfo {

    private String payInfo;//支付串码
    private String errMsg;//错误消息
    private String errCode;//错误码
    private String payType;//支付类型


    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return "PrePayInfo{" +
                "payInfo='" + payInfo + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", errCode='" + errCode + '\'' +
                ", payType='" + payType + '\'' +
                '}';
    }
}
