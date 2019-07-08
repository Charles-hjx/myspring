package com.hjx.spring.formework.webmvc.servlet;

import com.hjx.spring.formework.context.HJXApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 该servlet 只是作为一个MVC 的启动入口，所以还需要一个ApplicationContext 来启动 spring
 *
 * @Author: hjx
 * @Date: 2019/6/23 13:34
 * @Version 1.0
 */
public class DispatcherServlet  extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init() throws ServletException {
        HJXApplicationContext context = new HJXApplicationContext();
        super.init();
    }
}
