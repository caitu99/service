package com.caitu99.service.life;

import com.caitu99.service.life.domain.Activation;
import com.caitu99.service.life.domain.Product;
import com.caitu99.service.life.domain.ProductExchange;
import com.caitu99.service.life.service.ActivationService;
import com.caitu99.service.life.service.ProductExchangeService;
import com.caitu99.service.life.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Lion on 2015/12/1 0001.
 */
@Controller
@RequestMapping(value = "/api/migration/data")
public class MigrationData {
    private final static Logger logger = LoggerFactory.getLogger(MigrationData.class);

    @Autowired
    private ActivationService activationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductExchangeService productExchangeService;

    @RequestMapping(value = "/list/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getsqldata() {
        try {
            URL url = MigrationData.class.getResource("/");
            OutputStream out = new FileOutputStream(url.getPath() + "migrationdata.sql");
            List<Activation> activationlist = activationService.selectByAllData();
            List<Product> productList = productService.selectAll();
            List<ProductExchange> productExchangeList = productExchangeService.selectAll();
            //以前的所有的都统一放在一个类型里
            String sql = "insert into caitu99.t_type(id,name,create_time,update_time) values(1,'其他',now(),now());\r\n";
            String sql2 = "insert into caitu99.t_brand(brand_id,brand_no,name,create_time,update_time) values(1,1,'其他',now(),now());\r\n";
            out.write(sql.getBytes());
            out.write(sql2.getBytes());

            //商品
            getProduct(out, productList);

            //兑换券
            getActivateion(out, activationlist);
            //兑换记录
            getProductExchange(out, productExchangeList);
            out.close();
            System.out.println();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("error");
            return e.getMessage();
        }

        return "success";
    }

    private void getProductExchange(OutputStream out, List<ProductExchange> productExchangeList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (ProductExchange productExchange : productExchangeList) {

            Product product = productService.selectByPrimaryKey(productExchange.getProductid());
            Activation activation = activationService.selectByPrimaryKey(productExchange.getActivationid());
            if (product != null && activation != null) {
                try {
                    String sql = "insert into caitu99.t_receive_stock (id,user_id,stock_id,remote_type,remote_id,name,sale_price," +
                            "market_price,status,receive_time,create_time,update_time)" +
                            "values(" +
                            productExchange.getId() + ","
                            + productExchange.getUserid() + ","
                            + productExchange.getActivationid() + ","
                            + "2,"      //1 购买 2 赠送
                            + "'no_order_no',"
                            + "'" + product.getProduct() + "',"
                            + product.getPrice() + ","
                            + "500" + ","       //市场价
                            + "2,"      //2 领取
                            + "'" + sdf.format(productExchange.getTime()) + "',"
                            + "now(),now()" +
                            ");\r\n";

                    out.write(sql.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();

                }
            } else {
                System.out.println("error");
            }

        }
    }

    private void getActivateion(OutputStream out, List<Activation> activationlist) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (Activation activation : activationlist) {
            String sql = "insert into caitu99.t_stock (stock_id,item_id,sku_id,code,password,status,version," +
                    "effective_time,sale_time,create_time)" +
                    "values(" +
                    activation.getId() + ","        //stock_id
                    + activation.getType() + ","   //商品id '类型:productid', item_id
                    + activation.getType() + ","   //商品明细id sku_id
                    + "'" + activation.getActivation() + "',"
                    + "null,"
                    + activation.getStatus() + ","
                    + "'0.00',"
                    + "'" + sdf.format(activation.getValid()) + "',"
                    + "now(),now()" +
                    ");\r\n";
            try {
                out.write(sql.getBytes());
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public void getProduct(OutputStream out, List<Product> productList) {
        for (Product product : productList) {
            String str = "/GOODS/1/"+product.getId()+"/"+System.currentTimeMillis()+"/1.jpg";
            String sql = "insert into caitu99.t_item (item_id,title,sub_title,item_no,brand_id,sale_price," +
                    "market_price,content,version,status,pic_url,wap_url,sort,create_time,update_time)" +
                    "values(" +
                    product.getId() + ","
                    + "'" + product.getProduct() + "',"
                    + "'" + product.getSummary() + "',"
                    + product.getId() + ","
                    + "1," +
                    +product.getPrice() + ","   //财币
                    + "500" + ","       //分
                    + "'" + product.getDescription() + "'," //content
                    + "0.00,"
                    + "1,"
                    + "'" + str + "',"//pic
                    + "'http://static.caitu99.com/pages/goods-detail.html?itemId=" + product.getId() + "',"
                    + "1,now(),now()"
                    +
                    ");\r\n";
            String sql2 = "insert into caitu99.t_sku(sku_id,item_id,sale_price,cost_price,version,create_time,update_time)" +
                    "values(" +
                    product.getId() + "," +     //sku_id
                    product.getId() + "," +     //商品id
                    product.getPrice() + "," +  //单价单价
                    product.getPrice() + ","    //成本价
                    + "'0.00',now(),now()" +
                    ");\r\n";
            String sql3 = "insert into caitu99.t_type_item_relation(id,item_id,type_id,create_time,update_time)" +
                    "values(" +
                    product.getId() + ","
                    + product.getId() + ","
                    + "1,"
                    + "now(),now());\r\n";
            String sql4 = "insert into caitu99.t_attach_file(id,path,table_name,table_id,code,sort,status,create_time,update_time)"+
                    "values(" +
                    product.getId() +","+
                    "'"+str+"',"+
                    "'t_item',"+
                    product.getId()+","+
                    "'GOOD',"+
                    "1,"+
                    "1,"+
                    "now(),now()"+
                    ");\r\n";
            try {
                out.write(sql.getBytes());
                out.write(sql2.getBytes());
                out.write(sql3.getBytes());
                out.write(sql4.getBytes());
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
