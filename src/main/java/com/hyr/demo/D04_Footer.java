package com.hyr.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyStore.Entry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class D04_Footer {

//	public static final String SRC = "resources/pdfs/state.pdf";
	public static final String SRC = "data/fragment.pdf";
	public static final String DEST = String.format("target/a-%s.pdf",
			new SimpleDateFormat("dd-HH-mm-ss").format(new Date()));
	public static final String DATA = "data/united_states.csv";

	public static void main(String[] args) throws IOException, DocumentException {
		System.out.println("--start--");
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new D04_Footer().manipulatePdf2(SRC, DEST);
		System.out.println("--end--");
	}

	class MyFooter extends PdfPageEventHelper {
		public MyFooter() throws DocumentException, IOException {
			baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			ffont = new Font(baseFont, 15, Font.ITALIC);
		}

		BaseFont baseFont;
		Font ffont;
		// 模板
		public PdfTemplate total;

		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(50, 50);// 共 页 的矩形的长宽高
		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			Phrase footer = new Phrase(String.format("this is a page %s                                      sonylife",
					writer.getPageNumber()), ffont);
//			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header,
//					(document.right() - document.left()) / 2 + document.leftMargin(), document.top() + 10, 0);
//			ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, footer,
//					(document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PdfReader reader = new PdfReader("data/footer.pdf");
				PdfStamper stamper;
				AcroFields fields;
				stamper = new PdfStamper(reader, baos);
				fields = stamper.getAcroFields();
				fields.addSubstitutionFont(baseFont);
				fields.setField("page.number", "page" + writer.getPageNumber());
				fields.setField("label", "保険");
				PdfContentByte totalt = stamper.getOverContent(1);
				totalt.addTemplate(total, fields.getFieldPositions("page.count")[1],
						fields.getFieldPositions("page.count")[4]);
				stamper.setFormFlattening(true);
				stamper.close();
				reader.close();

				reader = new PdfReader(baos.toByteArray());
//				PdfReader reader = new PdfReader("data/footer.pdf");
				PdfImportedPage header = writer.getImportedPage(reader, 1);
				Image i = Image.getInstance(header);
				// set footer
				i.setAbsolutePosition(10, 10);
				cb.addImage(i);

				// set header
				i.setAbsolutePosition(10, document.top());
				cb.addImage(i);
			} catch (BadElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 *
		 * TODO 关闭文档时，替换模板，完成整个页眉页脚组件
		 *
		 */
		public void onCloseDocument(PdfWriter writer, Document document) {
			// 7.最后一步了，就是关闭文档的时候，将模板替换成实际的 Y 值,至此，page x of y 制作完毕，完美兼容各种文档size。
			total.beginText();
			BaseFont baseFont = null;
			try {
				baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			total.setFontAndSize(baseFont, 15f);// 生成的模版的字体、颜色
			String foot2 = " " + (writer.getPageNumber()) + " 页";
			total.showText(foot2);// 模版显示的内容
			total.endText();
			total.closePath();
		}
	}

	public void manipulatePdf2(String src, String dest) throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
//		ByteArrayOutputStream tempDest = new ByteArrayOutputStream();
//		PdfWriter writer = PdfWriter.getInstance(document, tempDest);

		BaseFont baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//		HeaderFooter foot = new HeaderFooter(new Phrase("-", new Font(baseFont)), new Phrase("-", new Font(baseFont)));
//		foot.setAlignment(2);
//		document.setFooter(foot);

		writer.setPageEvent(new MyFooter());
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

		int count = writer.getPageNumber();

		document.close();

//		reader = new PdfReader(tempDest.toByteArray());
//		stamper = new PdfStamper(reader, new FileOutputStream(dest + "3.pdf"));
//		fields = stamper.getAcroFields();
//		Set fs = fields.getFields().keySet();
//		for(Object str : fs) {
//			System.out.println(str);
//		}
//		fields.setField("page.count", count + "");
//		stamper.close();
//		reader.close();

	}
}
