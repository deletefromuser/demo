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
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.AcroFields.Item;
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

public class PolPrint {

	public static final String HEADER = "data/pol/polHeader.pdf";
	public static final String POL_TEMP = "data/pol/polFragment.pdf";
	public static final String SRC = "data/fragment.pdf";
	public static final String DEST = "target/a-result-pol.pdf";

	public static void main(String[] args) throws IOException, DocumentException {
		System.out.println("--start--");
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new PolPrint().manipulatePdf2(SRC, DEST);
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
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PdfReader reader = new PdfReader("data/footer.pdf");
				PdfStamper stamper;
				AcroFields fields;
				stamper = new PdfStamper(reader, baos);
				fields = stamper.getAcroFields();
				for (Object obj : fields.getFields().entrySet()) {
					Map.Entry<String, Item> entry = (Map.Entry<String, Item>) obj;
					// 获得块名
					String fieldName = entry.getKey();
					System.out.println(fieldName + ":");
				}
				fields.addSubstitutionFont(baseFont);
				fields.setField("page.number", "page" + writer.getPageNumber());
				fields.setField("label", "保険");
				float[] bs = fields.getFieldPositions("page.count");
				stamper.setFormFlattening(true);
				stamper.close();
				reader.close();

				reader = new PdfReader(baos.toByteArray());
				PdfImportedPage header = writer.getImportedPage(reader, 1);
				cb.addTemplate(header, 10, 10);
				cb.addTemplate(total, bs[1] + 5, bs[4] - 5);

			} catch (Exception e) {
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			total.setFontAndSize(baseFont, 15);// 生成的模版的字体、颜色
			String foot2 = " " + (writer.getPageNumber() - 1) + " page";
			total.showText(foot2);// 模版显示的内容
			total.endText();
			total.closePath();
		}
	}

	public void manipulatePdf2(String src, String dest) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4, 0, 0, 0, 0);
		printDocProperties(document);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));

		BaseFont baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//		HeaderFooter foot = new HeaderFooter(new Phrase("-", new Font(baseFont)), new Phrase("-", new Font(baseFont)));
//		foot.setAlignment(2);
//		document.setFooter(foot);

		writer.setPageEvent(new MyFooter());
		document.open();

		ByteArrayOutputStream baos;
		PdfReader reader;
		PdfStamper stamper;
		AcroFields fields;

		// Header
		baos = new ByteArrayOutputStream();
		reader = new PdfReader(HEADER);
		stamper = new PdfStamper(reader, baos);
		fields = stamper.getAcroFields();
		for (Object obj : fields.getFields().entrySet()) {
			Map.Entry<String, Item> entry = (Map.Entry<String, Item>) obj;
			// 获得块名
			String fieldName = entry.getKey();
			System.out.println(fieldName + ":");
		}
		fields.addSubstitutionFont(baseFont);
		fields.setField("custName", "山田　太郎");
		fields.setField("birthDate", "１９８０－０１－０２");
		fields.setField("address", "東京都あきる野市秋川１９８－０１００");
		fields.setField("tel", "9876543210");
		stamper.setFormFlattening(true);
		stamper.close();
		reader.close();

		reader = new PdfReader(baos.toByteArray());
		PdfImportedPage header = writer.getImportedPage(reader, 1);
		document.add(Image.getInstance(header));

		// Fragment
//		StringTokenizer tokenizer;
//		BufferedReader br = new BufferedReader(new FileReader(""));
//		String line = br.readLine();
//		while ((line = br.readLine()) != null) {
//			// create a PDF in memory
//			baos = new ByteArrayOutputStream();
//			reader = new PdfReader(SRC);
//			stamper = new PdfStamper(reader, baos);
//			fields = stamper.getAcroFields();
//			tokenizer = new StringTokenizer(line, ";");
//			fields.setField("name", tokenizer.nextToken());
//			fields.setField("abbr", tokenizer.nextToken());
//			fields.setField("capital", tokenizer.nextToken());
//			fields.setField("city", tokenizer.nextToken());
//			fields.setField("population", tokenizer.nextToken());
//			fields.setField("surface", tokenizer.nextToken());
//			fields.setField("timezone1", tokenizer.nextToken());
//			fields.setField("timezone2", tokenizer.nextToken());
//			fields.setField("dst", tokenizer.nextToken());
//
//			fields.addSubstitutionFont(baseFont);
//			fields.setField("name", "Daniel Reuter");
//			fields.setField("sex", "男性");
//			fields.setField("age", "35歳");
//			stamper.setFormFlattening(true);
//			stamper.close();
//			reader.close();
//
//			reader = new PdfReader(baos.toByteArray());
//			PdfImportedPage footer = writer.getImportedPage(reader, 1);
//			document.add(Image.getInstance(footer));
//		}
//		br.close();

		document.close();
	}

	void printDocProperties(Document doc) {
		System.out.println("---doc properties---");
		System.out.println("   page size: " + doc.getPageSize().toString());
		System.out.println("   margin left: " + doc.leftMargin());
		System.out.println("   margin right: " + doc.rightMargin());
		System.out.println("   margin top: " + doc.topMargin());
		System.out.println("   margin bottom: " + doc.bottomMargin());
		System.out.println("   left: " + doc.left());
		System.out.println("   right: " + doc.right());
		System.out.println("   top: " + doc.top());
		System.out.println("   bottom: " + doc.bottom());
	}
}
