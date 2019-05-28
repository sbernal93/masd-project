package upc.masd.environment;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

import jadex.commons.gui.SGUI;

/**
 *  Panel for showing the treasure hunter world view.
 */
class EnvironmentPanel extends JPanel
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    //-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"background", SGUI.makeIcon(EnvironmentPanel.class, "images/grass.jpg"),
        "resource", SGUI.makeIcon(EnvironmentPanel.class, "images/ore.png"),
        "agent", SGUI.makeIcon(EnvironmentPanel.class, "images/miner2.png"),
        "collected", SGUI.makeIcon(EnvironmentPanel.class, "images/flag.png"),
        "base", SGUI.makeIcon(EnvironmentPanel.class, "images/castle.png")
	});

	//-------- attributes --------
		
	/** The treasure hunter environment. */
	protected Environment	env;
	
	/** The paint-in-progress flag. */
	protected boolean	updating;
		
	//-------- constructors --------

	/**
	 *  Create a cleaner panel.
	 */
	public EnvironmentPanel(Environment env)
	{
		this.env	= env;
	}
	
	//-------- methods --------
	
	/**
	 *  Cause the panel to repaint.
	 */
	public void	environmentChanged()
	{
		if(!updating)
		{
			updating	= true;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					updating	= false;
					repaint();
				}
			});
		}
	}
	
	//-------- JPanel methods --------
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(800, 600);
	}

	/**
	 *  Paint the world view.
	 */
	protected void	paintComponent(Graphics g)
	{
		Rectangle	bounds	= getBounds();
		BufferedImage	img	= new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D	g2	= img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		
		// Paint background
		ImageIcon ii = (ImageIcon)icons.getIcon("background");
		g2.drawImage(ii.getImage(), 0, 0, bounds.width, bounds.height, this);
		
		paintIcon("base", new Rectangle2D.Double(env.getBaseLocation().getX()-0.2, env.getBaseLocation().getY()-0.2, 0.3, 0.3), g2);
		//System.out.println("gathered: " + env.gathered);
		g2.drawString("Total gathered: " + String.valueOf(env.gathered), (int) (env.getBaseLocation().getX() + 1), (int) (env.getBaseLocation().getY() + 100));

		// Paint collected treasures.
		Set<Resource>	collected;
		synchronized(env.collected)
		{
			 collected	= new LinkedHashSet(env.collected);
		}
		for(Resource t: collected)
		{
			Point2D	p	= t.location;
			paintIcon("collected", new Rectangle2D.Double(p.getX()-0.05, p.getY()-0.05, 0.1, 0.1), g2);
		}
		
		// Paint treasures.
		for(Resource t: env.getResources())
		{
			Point2D	p	= t.location;
			paintIcon("resource", new Rectangle2D.Double(p.getX()-0.05, p.getY()-0.05, 0.1, 0.1), g2);
		}
		
		// Paint the treasure hunter.
		Point2D	p	= env.agentLocation;
		paintIcon("agent", new Rectangle2D.Double(p.getX()-0.05, p.getY()-0.05, 0.1, 0.1), g2);
		
		g.drawImage(img, 0, 0, this);
	}

	/**
	 *  Convert env units to pixels.
	 */
	protected Rectangle	getPixelUnits(Rectangle2D bounds)
	{
		// Calculate size of objects depending on view size.
		Dimension	size	= getSize();
		double pixelperunit	= Math.min(size.width/Environment.WIDTH, size.height/Environment.HEIGHT);
		return new Rectangle((int)(bounds.getX()*pixelperunit), (int)(bounds.getY()*pixelperunit), (int)(bounds.getWidth()*pixelperunit), (int)(bounds.getHeight()*pixelperunit));
	}
	
	/**
	 *  Paint an icon.
	 */
	protected void	paintIcon(String icon, Rectangle2D bounds, Graphics2D g)
	{
		Rectangle	r	= getPixelUnits(bounds);
		ImageIcon ii = (ImageIcon)icons.getIcon(icon);
		g.drawImage(ii.getImage(), r.x, r.y, r.width, r.height, this);
	}
}
