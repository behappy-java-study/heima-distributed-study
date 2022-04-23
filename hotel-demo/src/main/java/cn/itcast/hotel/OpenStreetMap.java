package cn.itcast.hotel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 处理从OpenStreetMap下载的原始数据，将抽取的数据输出为txt文件
 * @author Administrator
 *
 */
public class OpenStreetMap {
    //遍历当前节点下的所有节点  
    @SuppressWarnings("unchecked")
    public static void listNodes(Element node) {
        System.out.println("当前节点的名称：" + node.getName());
        //首先获取当前节点的所有属性节点  
        List<Attribute> list = node.attributes();
        //遍历属性节点  
        for (Attribute attribute : list) {
            System.out.println("属性" + attribute.getName() + ":" + attribute.getValue());
        }
        //如果当前节点内容不为空，则输出  
        if (!(node.getTextTrim().equals(""))) {
            System.out.println(node.getName() + "：" + node.getText());
        }
        //同时迭代当前节点下面的所有子节点  
        //使用递归  
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException,
            DocumentException {
        // 点信息
        File pointFile = new File("D:\\BaiduYunDownload\\纽约出租数据\\15年1-6黄车-绿车数据\\yellow_tripdata_2015-06.csv\\Point.txt");
        // 弧信息
        File arcFile = new File("D:\\BaiduYunDownload\\纽约出租数据\\15年1-6黄车-绿车数据\\yellow_tripdata_2015-06.csv\\Arc.txt");
        FileOutputStream fosPoint = new FileOutputStream(pointFile);
        FileOutputStream fosArc = new FileOutputStream(arcFile);
        OutputStreamWriter oswPoint = new OutputStreamWriter(fosPoint);
        OutputStreamWriter oswArc = new OutputStreamWriter(fosArc);
        BufferedWriter bwPoint = new BufferedWriter(oswPoint);
        BufferedWriter bwArc = new BufferedWriter(oswArc);
        SAXReader reader = new SAXReader();

        // 要读取的原始地图数据
        String path = "D:\\BaiduYunDownload\\纽约出租数据\\15年1-6黄车-绿车数据\\yellow_tripdata_2015-06.csv\\map";
        Document document = reader.read(new File(path));

        // 获取根节点元素对象 osm
        Element root = document.getRootElement();
        // 遍历osm下的所有子节点
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
//            if(e.getName().equals("node")||e.getName().equals("way"))
//                listNodes(e);
            //输出点信息
            if (e.getName().equals("node")) {
                StringBuilder sb = new StringBuilder();
                //首先获取当前节点的所有属性节点  
                List<Attribute> list = e.attributes();
                //遍历属性节点  
                for (Attribute attribute : list) {
                    if (attribute.getName().equals("id")) {
                        sb.append(attribute.getValue() + "      ");
                    }
                    if (attribute.getName().equals("lat")) {
                        sb.append(attribute.getValue() + "      ");
                    }
                    if (attribute.getName().equals("lon")) {
                        sb.append(attribute.getValue());
                    }
                }
                bwPoint.write(sb.toString() + "\r\n");
                bwPoint.flush();
                System.out.println(sb.toString());
            } else if (e.getName().equals("way")) {  //输出弧信息
                StringBuilder sb = new StringBuilder();
                String s = "";
                //首先获取当前节点的所有属性节点  
                List<Attribute> list = e.attributes();
                //遍历属性节点  
                for (Attribute attribute : list) {
                    if (attribute.getName().equals("id")) {
                        s += attribute.getValue() + "      ";
                    }
                    if (attribute.getName().equals("version")) {
                        s += attribute.getValue() + "      ";
                    }
                }
                //遍历子节点
                Iterator<Element> iter = e.elementIterator();
                while (iter.hasNext()) {
                    Element element = iter.next();
                    //首先获取当前节点的所有属性节点  
                    List<Attribute> list1 = element.attributes();
                    //遍历属性节点  
                    for (Attribute attribute : list1) {
                        if (attribute.getName().equals("ref")) {
                            sb.append(s + attribute.getValue() + "      " + "\r\n");
                        } else if (attribute.getName().equals("k")) {
                            sb.append(s + "         " + attribute.getValue() + "\r\n");
                        }
                    }
                }
                bwArc.write(sb.toString());
                bwArc.flush();
                System.out.print(sb.toString());
            }
        }
        bwPoint.close();
        oswPoint.close();
        fosPoint.close();
        bwArc.close();
        oswArc.close();
        fosArc.close();
        System.out.println("输出完成！");
    }
}