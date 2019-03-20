package com.hyr.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

public class D04_FillFlattenMerge {

//	public static final String SRC = "resources/pdfs/state.pdf";
	public static final String SRC = "data/fragment.pdf";
	public static final String DEST = String.format("target/a-%s.pdf",
			new SimpleDateFormat("dd-HH-mm-ss").format(new Date()));
	public static final String DATA = "data/united_states.csv";

	public static void main(String[] args) throws IOException, DocumentException {
		System.out.println("--start--");
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new D04_FillFlattenMerge().manipulatePdf2(SRC, DEST);
		System.out.println("--end--");
	}

	public void manipulatePdf2(String src, String dest) throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));

		BaseFont baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		HeaderFooter foot = new HeaderFooter(new Phrase("-", new Font(baseFont)), new Phrase("-", new Font(baseFont)));
		foot.setAlignment(2);
		document.setFooter(foot);

		document.open();
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		PdfReader readerheader = new PdfReader("data/header.pdf");
		PdfImportedPage header = writer.getImportedPage(readerheader, 1);
//		PdfPCell cell = new PdfPCell(Image.getInstance(header));
//		cell.setBorder(PdfPCell.NO_BORDER);
//		cell.setColspan(1);
//		table.addCell(cell);
		document.add(Image.getInstance(header));

		ByteArrayOutputStream baos;
		PdfReader reader;
		PdfStamper stamper;
		AcroFields fields;
		StringTokenizer tokenizer;
		BufferedReader br = new BufferedReader(new FileReader(DATA));
		String line = br.readLine();
		while ((line = br.readLine()) != null) {
			// create a PDF in memory
			baos = new ByteArrayOutputStream();
			reader = new PdfReader(SRC);
			stamper = new PdfStamper(reader, baos);
			fields = stamper.getAcroFields();
			tokenizer = new StringTokenizer(line, ";");
			fields.setField("name", tokenizer.nextToken());
			fields.setField("abbr", tokenizer.nextToken());
			fields.setField("capital", tokenizer.nextToken());
			fields.setField("city", tokenizer.nextToken());
			fields.setField("population", tokenizer.nextToken());
			fields.setField("surface", tokenizer.nextToken());
			fields.setField("timezone1", tokenizer.nextToken());
			fields.setField("timezone2", tokenizer.nextToken());
			fields.setField("dst", tokenizer.nextToken());

			fields.addSubstitutionFont(baseFont);
			fields.setField("name", "Daniel Reuter");
			fields.setField("sex", "男性");
			fields.setField("age", "35歳");
			stamper.setFormFlattening(true);
			stamper.close();
			reader.close();

			reader = new PdfReader(baos.toByteArray());
			PdfImportedPage footer = writer.getImportedPage(reader, 1);
//			cell = new PdfPCell(Image.getInstance(footer));
//			cell.setBorder(PdfPCell.NO_BORDER);
//			cell.setColspan(1);
//			table.addCell(cell);
//			reader.close();
			document.add(Image.getInstance(footer));
		}
		br.close();
//        reader = new PdfReader("resources/pdfs/footer.pdf");
//        PdfImportedPage footer = writer.getImportedPage(reader, 1);
//        cell = new PdfPCell(Image.getInstance(footer));
//        cell.setColspan(3);
//        table.addCell(cell);
//		document.add(table);
		document.close();
	}
}
