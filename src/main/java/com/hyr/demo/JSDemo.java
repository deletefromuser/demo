package com.hyr.demo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PushbuttonField;

public class JSDemo {

//	public static final String SRC = "resources/pdfs/state.pdf";
	public static final String SRC = "data/fragment.pdf";
	public static final String DEST = String.format("target/a-%s.pdf",
			new SimpleDateFormat("dd-HH-mm-ss").format(new Date()));
	public static final String DATA = "data/united_states.csv";

	public static void main(String[] args) throws IOException, DocumentException {
		System.out.println("--start--");
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new JSDemo().manipulatePdf2(SRC, DEST);
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
				PdfReader reader = new PdfReader("data/image.pdf");
				PdfStamper stamper;
				AcroFields fields;
				stamper = new PdfStamper(reader, baos);
				fields = stamper.getAcroFields();
				fields.addSubstitutionFont(baseFont);
				fields.setField("label", "保険");
				PushbuttonField button = fields.getNewPushbuttonFromField("icon");
				button.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
				button.setProportionalIcon(true);
				button.setImage(Image.getInstance("data/01.jpg"));
				PdfFormField submit = button.getField();
				submit.setAction( PdfAction.javaScript("app.alert('hello,lec')", writer));
				stamper.addAnnotation(submit, 1);
				fields.replacePushbuttonField("icon", submit);
				
				stamper.setFormFlattening(true);
				stamper.close();
				reader.close();

				reader = new PdfReader(baos.toByteArray());
				PdfImportedPage header = writer.getImportedPage(reader, 1);
				Image i = Image.getInstance(header);
				cb.addTemplate(header, 10, 10);
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
			total.setFontAndSize(baseFont, 15);// 生成的模版的字体、颜色
			String foot2 = " " + (writer.getPageNumber() - 1) + " page";
			total.showText(foot2);// 模版显示的内容
			total.endText();
			total.closePath();
		}
	}

	public void manipulatePdf2(String src, String dest) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		printDocProperties(document);
		document.setJavaScript_onLoad("app.alert('test')");
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.setJavaScript_onLoad("app.alert('test')");

		BaseFont baseFont = BaseFont.createFont("data/msmincho.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		writer.setPageEvent(new MyFooter());
		document.open();
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		PdfReader readerheader = new PdfReader("data/header.pdf");
		PdfImportedPage header = writer.getImportedPage(readerheader, 1);
		document.add(Image.getInstance(header));

		writer.addJavaScript(PdfAction.javaScript("app.alert('hello,lec');this.close();", writer));
		document.setJavaScript_onLoad("app.alert('test');");
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
