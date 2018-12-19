package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.Navbar;

/**
 */
public class NavbarFigure extends Figure {

	private String[] texts = { Messages.NavbarFigure_0,
			Messages.NavbarFigure_1, Messages.NavbarFigure_2,
			Messages.NavbarFigure_3, Messages.NavbarFigure_4 };
	private static String simpleText = Messages.NavbarFigure_5;
	private Navbar model;
	private static Image bgImg = PrototypeImagesUtils.getImage("toolbar_bg.gif"); //$NON-NLS-1$
	private static Image navImg = PrototypeImagesUtils.getImage("navigation.gif"); //$NON-NLS-1$
	private static Image sepImg = PrototypeImagesUtils.getImage("toolbar_sep.gif"); //$NON-NLS-1$
	private static Image combImg = PrototypeImagesUtils
			.getImage("palette/itembar_01.png"); //$NON-NLS-1$

	public NavbarFigure() {
		setBorder(null);
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);

	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics g) {
		String type = model.getNavBarType();
		if (type.equals(Grid.NAVBAR_NONE))
			return;
		super.paintFigure(g);
		if (type.equals(Grid.NAVBAR_COMPLEX)) {
			paintComplex(g);
		} else {
			paintSimple(g);
		}
	}

	private void paintSimple(Graphics g) {
		Rectangle bounds = getBounds().getCopy();
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.drawLine(bounds.getTopLeft(), bounds.getTopRight());
		Dimension dim = FigureUtilities.getTextExtents(simpleText, getFont());
		g.setForegroundColor(ColorConstants.BLACK);
		g.drawString(simpleText, bounds.x + bounds.width - dim.width - 3,
				bounds.y + (bounds.height - dim.height) / 2);
	}

	private void paintComplex(Graphics g) {
		Rectangle bounds = getBounds().getCopy();
		g.drawImage(bgImg, new Rectangle(bgImg.getBounds()), bounds);
		// |<
		Rectangle r1 = new Rectangle(0, 0, 16, 16);
		Rectangle r2 = new Rectangle(bounds.x + bounds.width -380, bounds.y + 4, 16, 16);
		g.drawImage(navImg, r1, r2);
		// <
		r1.y = 32;
		r2.x += bounds.height;
		g.drawImage(navImg, r1, r2);
		int nextX = r2.x + r2.width + 2;	
//		 rect
		nextX += 2;
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.setBackgroundColor(ColorConstants.WHITE);
		Rectangle r4 = new Rectangle(nextX, bounds.y + 3, 30, bounds.height - 6);
		g.fillRectangle(r4);
		g.drawRectangle(r4);
		// 总页数
		Dimension dim = FigureUtilities.getTextExtents("/10", getFont());
		nextX =  r4.x + r4.width + 10;
		g.setForegroundColor(ColorConstants.BLACK);
		g.drawString("/10", nextX, bounds.y + (bounds.height - dim.height)/ 2);
		
		nextX += r4.width;
		g.setForegroundColor(ColorConstants.BLACK);
//		// >
		r1.y = 48;
		r2.x = nextX;
		g.drawImage(navImg, r1, r2);
		// >|
		r1.y = 16;
		r2.x += bounds.height;
		g.drawImage(navImg, r1, r2);
		
		nextX += r2.width+30;
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.setBackgroundColor(ColorConstants.WHITE);
		Rectangle r3 = new Rectangle(nextX, bounds.y + 3, 30, bounds.height - 6);
		g.fillRectangle(r3);
		g.drawRectangle(r3);
		
		nextX += r2.width+30;
		g.setForegroundColor(ColorConstants.BLACK);
		dim = FigureUtilities.getTextExtents("每页", getFont());
		g.drawString("每页", nextX, bounds.y + (bounds.height - dim.height)
				/ 2);
		
		nextX +=dim.width+ 10;
		g.setForegroundColor(ColorConstants.BLACK);
		dim = FigureUtilities.getTextExtents("显示条目 1 - 4 共 4", getFont());
		g.drawString("显示条目 1 - 4 共 4", nextX, bounds.y + (bounds.height - dim.height)
				/ 2);
		nextX +=dim.width+ 10;
		r1.y = 64;
		r2.x = nextX;
		g.drawImage(navImg, r1, r2);
	}

	public void setModel(Navbar model) {
		this.model = model;
	}

	private Image getImage(String key) {
		return PrototypeImagesUtils.getImage(key);
	}

}
