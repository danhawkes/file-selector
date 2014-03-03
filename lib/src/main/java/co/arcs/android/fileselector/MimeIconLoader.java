package co.arcs.android.fileselector;

import java.io.File;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

public class MimeIconLoader {

	private static final String PSEUDO_MIME_GENERIC = "generic";
	private static final String PSEUDO_MIME_AUDIO = "audio";
	private static final String PSEUDO_MIME_VIDEO = "video";
	private static final String PSEUDO_MIME_IMAGE = "image";
	private static final String PSEUDO_MIME_TEXT = "text";

	private final Map<String, Integer> mimeDrawableIds;
	private final Context context;

	public MimeIconLoader(Context context) {
		this.mimeDrawableIds = buildDrawableMap(context);
		this.context = context;
	}

	private Map<String, Integer> buildDrawableMap(Context context) {

		// Get relevant icon set style
		Theme theme = context.getTheme();
		TypedArray styleId = theme.obtainStyledAttributes(new int[] { R.attr.thumbnailIconStyle });
		int iconSetStyleId = styleId.getResourceId(0, R.style.FSThumbnailIconsDark);
		styleId.recycle();

		// Get drawable resource IDs within it
		TypedArray resIds = theme.obtainStyledAttributes(iconSetStyleId, R.styleable.ThumbnailIcon);
		int apkId = resIds.getResourceId(R.styleable.ThumbnailIcon_apk, 0);
		int audioId = resIds.getResourceId(R.styleable.ThumbnailIcon_audio, 0);
		int certificateId = resIds.getResourceId(R.styleable.ThumbnailIcon_certificate, 0);
		int codesId = resIds.getResourceId(R.styleable.ThumbnailIcon_code, 0);
		int compressedId = resIds.getResourceId(R.styleable.ThumbnailIcon_compressed, 0);
		int contactId = resIds.getResourceId(R.styleable.ThumbnailIcon_contact, 0);
		int directoryId = resIds.getResourceId(R.styleable.ThumbnailIcon_directory, 0);
		int eventId = resIds.getResourceId(R.styleable.ThumbnailIcon_event, 0);
		int fontId = resIds.getResourceId(R.styleable.ThumbnailIcon_font, 0);
		int genericId = resIds.getResourceId(R.styleable.ThumbnailIcon_generic, 0);
		int imageId = resIds.getResourceId(R.styleable.ThumbnailIcon_image, 0);
		int pdfId = resIds.getResourceId(R.styleable.ThumbnailIcon_pdf, 0);
		int presentationId = resIds.getResourceId(R.styleable.ThumbnailIcon_presentation, 0);
		int spreadsheetId = resIds.getResourceId(R.styleable.ThumbnailIcon_spreadsheet, 0);
		int textId = resIds.getResourceId(R.styleable.ThumbnailIcon_text, 0);
		int videoId = resIds.getResourceId(R.styleable.ThumbnailIcon_video, 0);
		resIds.recycle();

		// Add mime type mappings
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("application/vnd.android.package-archive", apkId);
		map.put("application/ogg", audioId);
		map.put("application/x-flac", audioId);
		map.put("application/pgp-keys", certificateId);
		map.put("application/pgp-signature", certificateId);
		map.put("application/x-pkcs12", certificateId);
		map.put("application/x-pkcs7-certreqresp", certificateId);
		map.put("application/x-pkcs7-crl", certificateId);
		map.put("application/x-x509-ca-cert", certificateId);
		map.put("application/x-x509-user-cert", certificateId);
		map.put("application/x-pkcs7-certificates", certificateId);
		map.put("application/x-pkcs7-mime", certificateId);
		map.put("application/x-pkcs7-signature", certificateId);
		map.put("application/rdf+xml", codesId);
		map.put("application/rss+xml", codesId);
		map.put("application/x-object", codesId);
		map.put("application/xhtml+xml", codesId);
		map.put("text/css", codesId);
		map.put("text/html", codesId);
		map.put("text/xml", codesId);
		map.put("text/x-c++hdr", codesId);
		map.put("text/x-c++src", codesId);
		map.put("text/x-chdr", codesId);
		map.put("text/x-csrc", codesId);
		map.put("text/x-dsrc", codesId);
		map.put("text/x-csh", codesId);
		map.put("text/x-haskell", codesId);
		map.put("text/x-java", codesId);
		map.put("text/x-literate-haskell", codesId);
		map.put("text/x-pascal", codesId);
		map.put("text/x-tcl", codesId);
		map.put("text/x-tex", codesId);
		map.put("application/x-latex", codesId);
		map.put("application/x-texinfo", codesId);
		map.put("application/atom+xml", codesId);
		map.put("application/ecmascript", codesId);
		map.put("application/json", codesId);
		map.put("application/javascript", codesId);
		map.put("application/xml", codesId);
		map.put("text/javascript", codesId);
		map.put("application/x-javascript", codesId);
		map.put("application/mac-binhex40", compressedId);
		map.put("application/rar", compressedId);
		map.put("application/zip", compressedId);
		map.put("application/x-apple-diskimage", compressedId);
		map.put("application/x-debian-package", compressedId);
		map.put("application/x-gtar", compressedId);
		map.put("application/x-iso9660-image", compressedId);
		map.put("application/x-lha", compressedId);
		map.put("application/x-lzh", compressedId);
		map.put("application/x-lzx", compressedId);
		map.put("application/x-stuffit", compressedId);
		map.put("application/x-tar", compressedId);
		map.put("application/x-webarchive", compressedId);
		map.put("application/x-webarchive-xml", compressedId);
		map.put("application/gzip", compressedId);
		map.put("application/x-7z-compressed", compressedId);
		map.put("application/x-deb", compressedId);
		map.put("application/x-rar-compressed", compressedId);
		map.put("text/x-vcard", contactId);
		map.put("text/vcard", contactId);
		map.put("text/calendar", eventId);
		map.put("text/x-vcalendar", eventId);
		map.put("application/x-font", fontId);
		map.put("application/font-woff", fontId);
		map.put("application/x-font-woff", fontId);
		map.put("application/x-font-ttf", fontId);
		map.put("application/vnd.oasis.opendocument.graphics", imageId);
		map.put("application/vnd.oasis.opendocument.graphics-template", imageId);
		map.put("application/vnd.oasis.opendocument.image", imageId);
		map.put("application/vnd.stardivision.draw", imageId);
		map.put("application/vnd.sun.xml.draw", imageId);
		map.put("application/vnd.sun.xml.draw.template", imageId);
		map.put("application/pdf", pdfId);
		map.put("application/vnd.ms-powerpoint", presentationId);
		map.put("application/vnd.openxmlformats-officedocument.presentationml.presentation",
				presentationId);
		map.put("application/vnd.openxmlformats-officedocument.presentationml.template",
				presentationId);
		map.put("application/vnd.openxmlformats-officedocument.presentationml.slideshow",
				presentationId);
		map.put("application/vnd.stardivision.impress", presentationId);
		map.put("application/vnd.sun.xml.impress", presentationId);
		map.put("application/vnd.sun.xml.impress.template", presentationId);
		map.put("application/x-kpresenter", presentationId);
		map.put("application/vnd.oasis.opendocument.presentation", presentationId);
		map.put("application/vnd.oasis.opendocument.spreadsheet", spreadsheetId);
		map.put("application/vnd.oasis.opendocument.spreadsheet-template", spreadsheetId);
		map.put("application/vnd.ms-excel", spreadsheetId);
		map.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", spreadsheetId);
		map.put("application/vnd.openxmlformats-officedocument.spreadsheetml.template",
				spreadsheetId);
		map.put("application/vnd.stardivision.calc", spreadsheetId);
		map.put("application/vnd.sun.xml.calc", spreadsheetId);
		map.put("application/vnd.sun.xml.calc.template", spreadsheetId);
		map.put("application/x-kspread", spreadsheetId);
		map.put("application/vnd.oasis.opendocument.text", textId);
		map.put("application/vnd.oasis.opendocument.text-master", textId);
		map.put("application/vnd.oasis.opendocument.text-template", textId);
		map.put("application/vnd.oasis.opendocument.text-web", textId);
		map.put("application/msword", textId);
		map.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", textId);
		map.put("application/vnd.openxmlformats-officedocument.wordprocessingml.template", textId);
		map.put("application/vnd.stardivision.writer", textId);
		map.put("application/vnd.stardivision.writer-global", textId);
		map.put("application/vnd.sun.xml.writer", textId);
		map.put("application/vnd.sun.xml.writer.global", textId);
		map.put("application/vnd.sun.xml.writer.template", textId);
		map.put("application/x-abiword", textId);
		map.put("application/x-kword", textId);
		map.put("application/x-quicktimeplayer", videoId);
		map.put("application/x-shockwave-flash", videoId);
		map.put("vnd.android.document/directory", directoryId);

		// Special cases
		map.put(PSEUDO_MIME_GENERIC, genericId);
		map.put(PSEUDO_MIME_AUDIO, audioId);
		map.put(PSEUDO_MIME_VIDEO, videoId);
		map.put(PSEUDO_MIME_IMAGE, imageId);
		map.put(PSEUDO_MIME_TEXT, textId);

		return map;
	}

	private static String getMimeType(File file) {
		if (file.isDirectory()) {
			return "vnd.android.document/directory";
		} else {
			return URLConnection.guessContentTypeFromName(file.getName());
		}
	}

	public Drawable loadMimeIcon(File file) {
		String mimeType = getMimeType(file);
		return loadMimeIcon(context, mimeType);
	}

	private Drawable loadMimeIcon(Context context, String mimeType) {

		Resources resources = context.getResources();

		Integer drawableId = mimeDrawableIds.get(mimeType);
		if (drawableId != null) {
			return resources.getDrawable(drawableId.intValue());
		} else {
			if (mimeType == null) {
				return resources.getDrawable(mimeDrawableIds.get(PSEUDO_MIME_GENERIC));
			} else {
				String str = mimeType.split("/")[0];
				if ("audio".equals(str)) {
					return resources.getDrawable(mimeDrawableIds.get(PSEUDO_MIME_AUDIO));
				} else if ("image".equals(str)) {
					return resources.getDrawable(mimeDrawableIds.get(PSEUDO_MIME_IMAGE));
				} else if ("text".equals(str)) {
					return resources.getDrawable(mimeDrawableIds.get(PSEUDO_MIME_TEXT));
				} else if ("video".equals(str)) {
					return resources.getDrawable(mimeDrawableIds.get(PSEUDO_MIME_VIDEO));
				} else {
					return resources.getDrawable(mimeDrawableIds.get(PSEUDO_MIME_GENERIC));
				}
			}
		}
	}
}
