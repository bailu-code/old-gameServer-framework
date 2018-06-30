package com.ts.framework.mail;

import java.io.IOException;

/**
 * Created by 37 on 2017/5/26.
 */
public class TestMail {

    public static void main(String[] args) throws IOException {
        MailHelper mailHelper = new MailHelper();
        mailHelper.openMail("mail.properties");
        mailHelper.sendMail("longtl_123@163.com","测试邮件发送情况", "服务器监控情况，定时报告");
    }

}
