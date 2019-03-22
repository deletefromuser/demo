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
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.junit.Test;

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
	public static final String FOOTER = "data/pol/polFooter.pdf";
	public static final String DEST = "target/a-result-pol.pdf";

	public static void main(String[] args) throws IOException, DocumentException {
		System.out.println("--start--");
		File file = new File(DEST);
		file.getParentFile().mkdirs();
		new PolPrint().manipulatePdf2(DEST);
		System.out.println("--end--");
	}

	class MyFooter extends PdfPageEventHelper {
		public MyFooter() throws DocumentException, IOException {
			baseFont = BaseFont.createFont("data/meiryo.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//			ffont = new Font(baseFont, 15, Font.ITALIC);
		}

		BaseFont baseFont;
//		Font ffont;
		// 模板
		public PdfTemplate total;

		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(26.7f, 17.04f);// 共 页 的矩形的长宽高
		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PdfReader reader = new PdfReader(FOOTER);
				PdfStamper stamper;
				AcroFields fields;
				stamper = new PdfStamper(reader, baos);
				fields = stamper.getAcroFields();

				System.out.println("Footer  :");
				for (Object obj : fields.getFields().entrySet()) {
					@SuppressWarnings("unchecked")
					Map.Entry<String, Item> entry = (Map.Entry<String, Item>) obj;
					// 获得块名
					String fieldName = entry.getKey();
					System.out.println("  -" + fieldName);
				}

				fields.addSubstitutionFont(baseFont);
				fields.setField("page.number", "" + writer.getPageNumber());
//				float[] bs1 = fields.getFieldPositions("page.number");
				float[] bs = fields.getFieldPositions("page.count");
				stamper.setFormFlattening(true);
				stamper.close();
				reader.close();

				reader = new PdfReader(baos.toByteArray());
				PdfImportedPage footer = writer.getImportedPage(reader, 1);
				cb.addTemplate(footer, 0, 10);
				cb.addTemplate(total, bs[1] + 0, bs[4] - 2);

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
				baseFont = BaseFont.createFont("data/meiryo.ttc,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			total.setFontAndSize(baseFont, 11);// 生成的模版的字体、颜色
			String foot2 = "" + (writer.getPageNumber() - 1);
			total.showText(foot2);// 模版显示的内容
			total.endText();
			total.closePath();
		}
	}

	public void manipulatePdf2(String dest) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4, 0, 0, 0, 0);
		printDocProperties(document);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));

		BaseFont baseFont = BaseFont.createFont("data/meiryo.ttc,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED); // msmincho
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

		System.out.println("Header  :");
		for (Object obj : fields.getFields().entrySet()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, Item> entry = (Map.Entry<String, Item>) obj;
			// 获得块名
			String fieldName = entry.getKey();
			System.out.println("  -" + fieldName);
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

//		 Fragment
		ArrayList<PolData> pols = getPols();
		for (PolData inDto : pols) {
			// create a PDF in memory
			baos = new ByteArrayOutputStream();
			reader = new PdfReader(POL_TEMP);
			stamper = new PdfStamper(reader, baos);
			fields = stamper.getAcroFields();

			System.out.println("Fragment  :");
			for (Object obj : fields.getFields().entrySet()) {
				@SuppressWarnings("unchecked")
				Map.Entry<String, Item> entry = (Map.Entry<String, Item>) obj;
				// 获得块名
				String fieldName = entry.getKey();
				System.out.println("  -" + fieldName);
			}

			fields.addSubstitutionFont(baseFont);

			fields.setField("pol", inDto.getPol());
			fields.setField("keiyaName", inDto.getKeiyaName());
			fields.setField("hiName", inDto.getHiName());
			fields.setField("masterKeiya", inDto.getMasterKeiya());
			fields.setField("lnnnTokuyaku", inDto.getLnnnTokuyaku());
			fields.setField("menjyoToku", inDto.getMenjyoToku());
			fields.setField("toriField", inDto.getToriField());
			fields.setField("toriKind", inDto.getToriKind());
			fields.setField("toriName", inDto.getToriName());
			fields.setField("kashiDate", inDto.getKashiDate());
			fields.setField("aplCap", inDto.getAplCap());
			fields.setField("aplCapUnit", inDto.getAplCapUnit());
			fields.setField("plCap", inDto.getPlCap());
			fields.setField("plCapUnit", inDto.getPlCapUnit());
			fields.setField("plInt", inDto.getPlInt());
			fields.setField("plIntUnit", inDto.getPlIntUnit());
			fields.setField("aplInt", inDto.getAplInt());
			fields.setField("aplIntUnit", inDto.getAplIntUnit());
			fields.setField("tokuField", inDto.getTokuField());
			fields.setField("tokuName", inDto.getTokuName());
			fields.setField("genkyo", inDto.getGenkyo());
			fields.setField("moneyTitle", inDto.getMoneyTitle());
			fields.setField("moneyAmount", inDto.getMoneyAmount());
			fields.setField("keiyaDate", inDto.getKeiyaDate());
			fields.setField("finishDate", inDto.getFinishDate());
			fields.setField("declineSpan", inDto.getDeclineSpan());
			fields.setField("notInsuOrgan", inDto.getNotInsuOrgan());

			stamper.setFormFlattening(true);
			stamper.close();
			reader.close();

			reader = new PdfReader(baos.toByteArray());
			PdfImportedPage footer = writer.getImportedPage(reader, 1);
			document.add(Image.getInstance(footer));
		}

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

	ArrayList<PolData> getPols() {
		ArrayList<PolData> list = new ArrayList<>();
		int i = 1;

		for (i = 1; i < 7; i++) {
			PolData newpd = new PolData();

			newpd.setPol("p123456789" + String.valueOf(i));
			newpd.setKeiyaName("契約者名" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setHiName("被保険者名" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setMasterKeiya("主契約" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setLnnnTokuyaku("ＬＮ／ＮＮ　特約" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setMenjyoToku("保険料免除特約" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setToriField(String.valueOf(i));
			newpd.setToriKind("受取人種別" + String.valueOf(i));
			newpd.setToriName("受取人名" + String.valueOf(i));
			newpd.setKashiDate("１９８０－０１－０" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setAplCap("10236" + String.valueOf(i));
			newpd.setAplCapUnit("ドル");
			newpd.setPlCap("99999" + String.valueOf(i));
			newpd.setPlCapUnit("円");
			newpd.setPlInt("236" + String.valueOf(i));
			newpd.setPlIntUnit("円");
			newpd.setAplInt("215" + String.valueOf(i));
			newpd.setAplIntUnit("ドル");
			newpd.setTokuField(String.valueOf(i));
			newpd.setTokuName("特約名" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setGenkyo("ＮＯＴＧＯＯＤ" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setMoneyTitle("ある金" + changeNumHalfToFull(String.valueOf(i)));
			newpd.setMoneyAmount("52874" + String.valueOf(i));
			newpd.setKeiyaDate("1985-06-0" + String.valueOf(i));
			newpd.setFinishDate("1990-06-0" + String.valueOf(i));
			newpd.setDeclineSpan("0" + String.valueOf(i));
			newpd.setNotInsuOrgan("0" + String.valueOf(i));

			list.add(newpd);
		}
		return list;
	}

	/**
	 * <p>
	 * [概 要] 半角数字⇒全角数字への変換
	 * </p>
	 * <p>
	 * [詳 細]
	 * </p>
	 * <p>
	 * [備 考]
	 * </p>
	 * 
	 * @param str 変換対象文字列
	 * @return 変換後文字列
	 */
	public static String changeNumHalfToFull(String str) {
		String result = null;
		if (str != null) {
			StringBuilder sb = new StringBuilder(str);
			for (int i = 0; i < sb.length(); i++) {
				int c = (int) sb.charAt(i);
				if (c >= 0x30 && c <= 0x39) {
					sb.setCharAt(i, (char) (c + 0xFEE0));
				}
			}
			result = sb.toString();
		}
		return result;
	}

}
