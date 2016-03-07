package com.cleverm.smartpen.util.excle;

import android.os.Environment;

import com.cleverm.smartpen.util.QuickUtils;
import com.cleverm.smartpen.util.StatisticsUtil;

import java.io.File;
import java.io.IOException;

import jxl.LabelCell;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by xiong,An android project Engineer,on 2016/3/7.
 * Data:2016-03-07  15:46
 * Base on clever-m.com(JAVA Service)
 * Describe: 使用jxl-jar创建excel文件
 * Version:1.0
 * Open source
 */
public class CreateExcel {

    private static final String EXPORT_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/muyefile";

    //excle工作表的标题
    private WritableSheet sheet;
    //excle工作簿
    private WritableWorkbook wwb;
    //excle第一行
    private String[] title ={"事件编号","点击时间(秒)","停留时间(秒)","客户编号","餐厅编号","桌位编号","描述备注","二级事件编号","统计时间"};


    public CreateExcel(String[] colName){
        init(colName);
    }

    public static WritableFont text14Font = null;
    public static WritableCellFormat text14Format=null;

    public static WritableFont text12Font = null;
    public static WritableCellFormat text12Format=null;

    public static WritableFont text10Font = null;
    public static WritableCellFormat text10Format=null;

    //制定表单的格式:字体大小，字体颜色等配置
    public static void format(){

        text14Font = new WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD);
        text12Font = new WritableFont(WritableFont.ARIAL, 12);
        text10Font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);

        try {
            //第一行的样式
            text14Font.setColour(Colour.LIGHT_BLUE);
            text14Format = new WritableCellFormat(text14Font);
            text14Format.setAlignment(Alignment.CENTRE);
            text14Format.setBorder(Border.ALL, BorderLineStyle.THIN);
            text14Format.setBackground(Colour.VERY_LIGHT_YELLOW);

            //第二行的样式
            text12Font.setColour(Colour.BLUE);
            text12Format = new WritableCellFormat(text12Font);
            text12Format.setBorder(Border.ALL, BorderLineStyle.THIN);

            //数据行的样式
            text10Font.setColour(Colour.BLUE);
            text10Format = new WritableCellFormat(text10Font);
            text10Format.setAlignment(Alignment.CENTRE);
            text10Format.setBorder(Border.ALL, BorderLineStyle.THIN);
            text10Format.setBackground(Colour.LIGHT_BLUE);


        } catch (WriteException e) {
            e.printStackTrace();
        }

    }



    /**
     * 先写Excle第一行和第二行:
     * 第一行是:2016-03-27 统计报表
     * 第二行是:
     * @param colName
     */
    private void init(String[] colName)  {
        format();
        try {
            File file = new File(EXPORT_PATH + StatisticsUtil.getInstance().getEventHappenTime() + ".xls");
            if(!file.exists()){
                file.createNewFile();
            }
            wwb = Workbook.createWorkbook(file);
            //添加第一个工作表Sheet
            sheet = wwb.createSheet("" + StatisticsUtil.getInstance().getEventHappenTime(), 0);

            //定义一行单元格
            WritableCell writableCell_first_line = new Label(0, 0, StatisticsUtil.getInstance().getEventHappenTime() + "  统计报表", text14Format);
            sheet.addCell(writableCell_first_line);

            //定义第二行单元格
            for(int i =0 ; i < colName.length; i++){
                sheet.addCell(new Label(i,0,title[i],text12Format));
            }

            //写操作
            wwb.write();

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //无论如何都关闭close
            if(wwb!=null){
                try {
                    wwb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }





}
