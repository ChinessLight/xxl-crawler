package com.xuxueli.crawler.test;

import com.xuxueli.crawler.XxlCrawler;
import com.xuxueli.crawler.annotation.PageFieldSelect;
import com.xuxueli.crawler.annotation.PageSelect;
import com.xuxueli.crawler.conf.XxlCrawlerConf;
import com.xuxueli.crawler.parser.PageParser;
import com.xuxueli.crawler.util.FileUtil;
import com.xuxueli.crawler.util.JsoupUtil;
import org.jsoup.nodes.Document;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 爬虫示例03：爬取页面，下载图片文件
 *
 * @author xuxueli 2017-10-09 19:48:48
 */
public class XxlCrawlerTest03 {

    @PageSelect(".body")
    public static class PageVo {

        @PageFieldSelect(value = "#blogBody img", valType = "attr", attributeKey = "abs:src")
        private List<String> images;

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        @Override
        public String toString() {
            return "PageVo{" +
                    "images=" + images +
                    '}';
        }
    }

    public static void main(String[] args) {

        XxlCrawler crawler = new XxlCrawler.Builder()
                .setUrls(new HashSet<String>(Arrays.asList("https://my.oschina.net/xuxueli/blog")))
                .setWhiteUrlRegexs(new HashSet<String>(Arrays.asList("https://my\\.oschina\\.net/xuxueli/blog/\\d+")))
                .setThreadCount(1)
                .setPageParser(new PageParser<PageVo>() {
                    @Override
                    public void parse(String url, Document html, PageVo pageVo) {

                        System.out.println(pageVo);
                        if (pageVo != null) {
                            return;
                        }

                        // 文件信息
                        String filePath = "/Users/xuxueli/Downloads/tmp";

                        Set<String> images = JsoupUtil.findImages(html);
                        if (images.size() > 0) {
                            for (String img: images) {

                                // 下载图片文件
                                String fileName = FileUtil.getFileNameByUrl(img, null);
                                boolean ret = FileUtil.downFile(img, XxlCrawlerConf.TIMEOUT_MILLIS_DEFAULT, filePath, fileName);
                                System.out.println("down images" + (ret?"success":"fail") + "：" + img);
                            }
                        }
                    }
                })
                .build();

        System.out.println("start");
        crawler.start(true);
        System.out.println("end");
    }

}