package com.hyr.demo;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.IOException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

public class ItextDemo {
	public static void main(String[] args) {
		System.out.println("--start--");
//		String pdfTemplateFile = "data/New_Blank_Document.pdf";
		String pdfTemplateFile = "data/polDesp.pdf";

		try {
			// Initialize PDF document
			PdfDocument pdf = new PdfDocument(new PdfReader(pdfTemplateFile), new PdfWriter(
					String.format("target/a-%s.pdf", new SimpleDateFormat("dd-HH-mm-ss").format(new Date()))));
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
			Map<String, PdfFormField> fields = form.getFormFields();

			PdfFont baseFont = PdfFontFactory.createFont("data/msgothic.ttc,1", "Identity-H", false);
//		BaseFont baseFont = BaseFont.createFont("data/meiryob.ttc,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

			int i = 0;
			java.util.Iterator<String> it = fields.keySet().iterator();
			while (it.hasNext()) {
				// 获取文本域名称
				String name = it.next().toString();
				switch (i++) {
				case 0:

					// 填充文本域
					fields.get(name).setValue("Windows 田中　太郎ＰP123");
					break;
				case 1:
					// 填充文本域
					fields.get(name).setValue("Windows 田中　太郎ＰP12335歳");
					break;
				case 2:
					// 填充文本域
					fields.get(name).setValue("Windows 田中　太郎ＰP123男性");
					break;
				}
				fields.get(name).setFont(baseFont);
			}
			form.flattenFields();// 设置表单域不可编辑
			pdf.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
//	static public void fillTableDatas() throws Exception {
//
//		// 1-模板和生成的pdf
//
//		String tPdfTemplateFile = "data/New_Blank_Document.pdf";// 获取模板路径
//		String tPdfResultFile = String.format("target/a-%s.pdf",
//				new SimpleDateFormat("dd-HH-mm-ss").format(new Date()));// 生成的文件路径
//
//		// 2-解析PDF模板
//		FileOutputStream fos = new FileOutputStream(tPdfResultFile);// 需要生成PDF
//		PdfReader reader = new PdfReader(tPdfTemplateFile);// 模板
//		PdfStamper mPdfStamper = new PdfStamper(reader, fos);// 解析
//
//		// 3-获取到模板上预定义的参数域
//		AcroFields form = mPdfStamper.getAcroFields();
//		BaseFont baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//		form.addSubstitutionFont(baseFont);
//		// 获取模板中定义的变量
//		Map<String, Item> acroFieldMap = form.getFields();
//
//		// 循环解析模板定义的表单域
//		int len = 4;
//		for (Map.Entry<String, Item> entry : acroFieldMap.entrySet()) {
//			// 获得块名
//			String fieldName = entry.getKey();
//			String fieldValue = "fill_" + len;
//			System.out.println(fieldName + ":" + fieldValue);
//			form.setField(fieldName, fieldValue);
//			len++;
//		}
//
//		// 模板中的变量赋值之后不能编辑
//		mPdfStamper.setFormFlattening(true);
//		mPdfStamper.close();
//		reader.close();// 阅读器关闭,解析器暂时不关闭，因为创建动态表格还需要使用
//
//	}

}
