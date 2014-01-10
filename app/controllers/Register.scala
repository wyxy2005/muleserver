//package controllers
//
//import play.mvc.{Controller}
//import play.mvc.results.{Ok, Template}
//
//import play.templates.Html
//import models.Service
//
///**
// *
// * 功能描述：
// * <p> 版权所有：优视科技
// * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
// *
// * @author <a href="mailto:liuyj3@ucweb.com">刘永健</a>
// * @version 1.0.0
// * @since 1.0.0
// *        create on: 2014年01月07
// */
//object Register extends Controller {
//  def OutputOK = Ok
//
//  def hello = Html("<h1>Hello world</h1>")
//
//  def deployService(zipFile: java.io.File, name: String) = {
//    if (request.method == "GET") {
//      Template('name -> name)
//    } else {
//      listService();
//    }
//  }
//
//  def listService() = {
//    val list  = Service.findAll();
//    println(list)
//    Ok
////    Template('serviceList -> list)
//  }
//
//
//}
