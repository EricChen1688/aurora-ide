package aurora.ide.meta.gef.editors.figures;

import java.util.Arrays;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.util.BoundsConvert;
import aurora.ide.meta.gef.util.MessageUtil;
import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

/**
 */
public class ButtonFigure extends Figure implements IResourceDispose {

	private static Image bgImg = PrototypeImagesUtils.getImage("btn.gif");
	private static String[] buttonTypes = { Button.ADD, Button.SAVE,
			Button.DELETE, Button.CLEAR, Button.EXCEL };
	private static Image stdimg = PrototypeImagesUtils
			.getImage("aurora/toolbar_btn.gif");
	private static Image defaultimg = PrototypeImagesUtils
			.getImage("aurora/default.gif");

	private Button model = null;

	public ButtonFigure() {
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
		g.pushState();
		Rectangle rect = getBounds();
		Dimension dim = BoundsConvert.getSize(model);
		IFigure parentFigure = getParent();
//		if (!(parentFigure instanceof ToolbarFigure)) {
			g.drawImage(bgImg, 0, 0, 3, 2, rect.x, rect.y, 3, 2);// tl
			g.drawImage(bgImg, 0, 6, 1, 2, rect.x + 3, rect.y, dim.width - 6, 2);// tc
			g.drawImage(bgImg, 3, 0, 3, 2, rect.x + dim.width - 3, rect.y, 3, 2);// tr
			g.drawImage(bgImg, 0, 24, 3, 1, rect.x, rect.y + 2, 3,
					dim.height - 4);// ml
			g.drawImage(bgImg, 3, 24, 3, 1, rect.x + dim.width - 3, rect.y + 2,
					3, dim.height - 4);// mr
			g.drawImage(bgImg, 0, 1096, 1, dim.height - 4, rect.x + 3,
					rect.y + 2, dim.width - 6, dim.height - 4);// mc
			g.drawImage(bgImg, 0, 4, 3, 2, rect.x, rect.y + dim.height - 2, 3,
					2);// bl
			g.drawImage(bgImg, 0, 16, 1, 2, rect.x + 3,
					rect.y + dim.height - 2, dim.width - 3, 2);// bc
			g.drawImage(bgImg, 3, 4, 3, 2, rect.x + dim.width - 3, rect.y
					+ dim.height - 2, 3, 2);// br
//		}
		String text = MessageUtil.getButtonText(model);
		Dimension textExtents = FigureUtilities.getTextExtents(text, getFont());
		Rectangle r1 = null;
		//		Rectangle r1 = getStdImgRect();
		g.setForegroundColor(ColorConstants.WHITE);
		if (r1 == null) {

			if (TextStyleUtil.isTextLayoutUseless(this.model,
					ComponentProperties.text) == false) {
				paintStyledText(g, text, ComponentProperties.text, rect.x
						+ (dim.width - textExtents.width) / 2, rect.y
						+ (dim.height - textExtents.height) / 2);
			} else {
				g.drawString(text,
						rect.x + (dim.width - textExtents.width) / 2, rect.y
								+ (dim.height - textExtents.height) / 2);
			}

		} else {
			Rectangle r2 = new Rectangle(rect.x
					+ (dim.width - textExtents.width - 16) / 2, rect.y
					+ (dim.height - r1.height) / 2, 16, 17);
			g.drawImage(getBgImage(), r1, r2);
			g.drawString(text,
					rect.x + (dim.width - textExtents.width) / 2 + 8, rect.y
							+ (dim.height - textExtents.height) / 2);
		}
		g.popState();
	}

	protected void paintStyledText(Graphics g, String text, String property_id,
			int x, int y) {
		g.pushState();
		this.disposer.disposeResource(property_id);
		TextLayout tl = new TextLayout(null);
		tl.setText(text);
		tl.setFont(getFont());
		Object obj = model.getPropertyValue(property_id
				+ ComponentInnerProperties.TEXT_STYLE);
		TextStyle ts = null;
		if (obj instanceof StyledStringText) {
			ts = TextStyleUtil.createTextStyle((StyledStringText) obj,
					Display.getDefault(), getFont());
		} else {
			ts = new TextStyle();
		}
		tl.setStyle(ts, 0, text.length() - 1);
		g.drawTextLayout(tl, x, y);
		this.disposer.handleResource(property_id, tl);
		g.popState();
	}

	private Rectangle getStdImgRect() {
		String btype = model.getButtonType();
		for (int i = 0; i < buttonTypes.length; i++) {
			if (buttonTypes[i].equals(btype)) {
				return new Rectangle(0, 17 * i, 16, 17);
			}
		}
		if (model.getIcon() == null || model.getIcon().length() == 0)
			return null;
		return new Rectangle(defaultimg.getBounds());
	}

	private Image getBgImage() {
		return isStdButton(model) ? stdimg : defaultimg;
	}

	private boolean isStdButton(Button model) {
		return Arrays.asList(Button.std_types).indexOf(model.getButtonType()) > 0;
	}

	public void setModel(Button model) {
		this.model = model;
	}

	private ResourceDisposer disposer = new ResourceDisposer();

	public void disposeResource() {
		disposer.disposeResource();
		disposer = null;
	}
}
