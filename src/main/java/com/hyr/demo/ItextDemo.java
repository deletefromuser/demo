package com.hyr.demo;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class ItextDemo {
	public static void main(String[] args) {
		System.out.println("--start--");
		String pdfTemplateFile = "data/temp.pdf";
		try {
			PdfReader pdfTemplate = new PdfReader(pdfTemplateFile);
			FileOutputStream out = new FileOutputStream(
					String.format("target/a-%s.pdf", new SimpleDateFormat("HH-mm-ss").format(new Date())));
			PdfStamper stamper = new PdfStamper(pdfTemplate, out);
			AcroFields fields = stamper.getAcroFields();

			fields.setField("name", "Daniel Reuter");
			BaseFont baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			fields.setFieldProperty("sex", "textfont", baseFont, null);
			fields.setField("sex", "男性");
			fields.setFieldProperty("age", "textfont", baseFont, null);
			fields.setField("age", "35歳");

			stamper.setFormFlattening(true);

			stamper.close();
			pdfTemplate.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("--end--");
		}

	}

}
