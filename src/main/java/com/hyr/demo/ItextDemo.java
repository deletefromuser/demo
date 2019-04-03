package com.hyr.demo;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class ItextDemo {
	public static void main(String[] args) {
		System.out.println("--start--");
		String pdfTemplateFile = "data/New_Blank_Document.pdf";
//		String pdfTemplateFile = "data/form.pdf";
		try {
			PdfReader pdfTemplate = new PdfReader(pdfTemplateFile);
			FileOutputStream out = new FileOutputStream(
					String.format("target/a-%s.pdf", new SimpleDateFormat("dd-HH-mm-ss").format(new Date())));
			PdfStamper stamper = new PdfStamper(pdfTemplate, out);
			AcroFields fields = stamper.getAcroFields();

			BaseFont baseFont = BaseFont.createFont("data/msgothic.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//			BaseFont baseFont = BaseFont.createFont("data/meiryob.ttc,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			fields.addSubstitutionFont(baseFont);
			for (Entry<String, Item> map : fields.getFields().entrySet()) {
				fields.setFieldProperty(map.getKey(), "textfont", baseFont, null);
			}
			fields.setField("name", "Windows 田中　太郎ＰP123");
//			fields.setFieldProperty("sex", "textfont", baseFont, null);
			fields.setField("sex", "男性");
//			fields.setFieldProperty("age", "textfont", baseFont, null);
			fields.setField("age", "35歳");
//			fields.setFieldProperty("text", "textfont", baseFont, null);
			fields.setField("text", "35歳");

			fields.setField("Name", "Raf Hens男性");
			fields.setField("Company", "iText Software35歳");
			fields.setField("Country", "BELGIUM35歳获取保单域（ AcroFields ）");

			stamper.setFormFlattening(true);

			stamper.close();
			pdfTemplate.close();

			//
//			Thread.sleep(1000);
//			fillTableDatas();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("--end--");
		}

	}

	/**
	 * 给PDF表格赋值 值动态的，一般建议使用模板， 直接创建绝对位置的表格
	 * 
	 * @author ShaoMin
	 * @throws Exception
	 */
	static public void fillTableDatas() throws Exception {

		// 1-模板和生成的pdf

		String tPdfTemplateFile = "data/New_Blank_Document.pdf";// 获取模板路径
		String tPdfResultFile = String.format("target/a-%s.pdf",
				new SimpleDateFormat("dd-HH-mm-ss").format(new Date()));// 生成的文件路径

		// 2-解析PDF模板
		FileOutputStream fos = new FileOutputStream(tPdfResultFile);// 需要生成PDF
		PdfReader reader = new PdfReader(tPdfTemplateFile);// 模板
		PdfStamper mPdfStamper = new PdfStamper(reader, fos);// 解析

		// 3-获取到模板上预定义的参数域
		AcroFields form = mPdfStamper.getAcroFields();
		BaseFont baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		form.addSubstitutionFont(baseFont);
		// 获取模板中定义的变量
		Map<String, Item> acroFieldMap = form.getFields();

		// 循环解析模板定义的表单域
		int len = 4;
		for (Map.Entry<String, Item> entry : acroFieldMap.entrySet()) {
			// 获得块名
			String fieldName = entry.getKey();
			String fieldValue = "fill_" + len;
			System.out.println(fieldName + ":" + fieldValue);
			form.setField(fieldName, fieldValue);
			len++;
		}

		// 模板中的变量赋值之后不能编辑
		mPdfStamper.setFormFlattening(true);
		mPdfStamper.close();
		reader.close();// 阅读器关闭,解析器暂时不关闭，因为创建动态表格还需要使用

	}

}
