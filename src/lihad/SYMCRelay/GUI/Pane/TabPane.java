package lihad.SYMCRelay.GUI.Pane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.Adapters.ChangeAdapter;

import com.alee.laf.tabbedpane.WebTabbedPane;

public class TabPane extends WebTabbedPane{

	private static final long serialVersionUID = -3629700175117007156L;

	private boolean dragging = false;
	private Image tabImage = null;
	private Component component;
	private String title;
	private Point currentMouseLocation = null;
	private int draggedTabIndex = 0;

	public String[] getTabNames(){
		List<String> l_t = new LinkedList<String>();
		for(int i = 0; i < TabPane.this.getTabCount(); i++){
			l_t.add(TabPane.this.getTitleAt(i).replace("#", ""));
		}
		return l_t.toArray(new String[0]);
	}
	
	public TabPane(){
		this.addChangeListener(new ChangeAdapter(){
			public void stateChanged(ChangeEvent e) {
				if(TabPane.this.getSelectedIndex() != -1){
					setFlash(false, TabPane.this.getSelectedIndex());
				}
			}
		});
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent event) {
				if(SwingUtilities.isRightMouseButton(event)){
					int index = TabPane.this.indexAtLocation(event.getX(), event.getY());

					//TODO: similar code
					Client.channelLeaveRequest(TabPane.this.getTitleAt(index).replace("#", ""));
					Client.getRelayConfiguration().removeDefaultChannel(TabPane.this.getTitleAt(index).replace("#", ""));
					Client.channels.remove(Client.getChannel(TabPane.this.getTitleAt(index).replace("#", "")));
					TabPane.this.remove(index);
				}else if(SwingUtilities.isLeftMouseButton(event)){
					int index = TabPane.this.indexAtLocation(event.getX(), event.getY());
					if(index >= 0){
						Client.getChannel(TabPane.this.getTitleAt(index).replace("#", "")).field.requestFocusInWindow();
						Client.gui.userPane.expandChannel(TabPane.this.getTitleAt(index).replaceFirst("#", ""));
					}
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if(dragging) {
					int tabNumber = getUI().tabForCoordinate(TabPane.this, e.getX(), 10);

					if(tabNumber >= 0) {
						insertTab(title, null, component, null, tabNumber);
						TabPane.this.setSelectedIndex(tabNumber);
					}else{
						insertTab(title, null, component, null, TabPane.this.getTabCount());
						TabPane.this.setSelectedIndex(TabPane.this.getTabCount()-1);
					}

					Client.gui.userPane.orderNodes(getTabNames());
					Client.gui.userPane.expandChannel(title.replaceFirst("#", ""));
					TabPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

				dragging = false;
				tabImage = null;

			}

		});
		this.addMouseMotionListener(new MouseMotionAdapter(){

			public void mouseDragged(MouseEvent e) {
				if(!dragging) {
					// Gets the tab index based on the mouse position
					int tabNumber = getUI().tabForCoordinate(TabPane.this, e.getX(), e.getY());

					if(tabNumber >= 0) {
						draggedTabIndex = tabNumber;
						Rectangle bounds = getUI().getTabBounds(TabPane.this, tabNumber);

						// Paint the tabbed pane to a buffer
						Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics totalGraphics = totalImage.getGraphics();
						totalGraphics.setClip(bounds);
						// Don't be double buffered when painting to a static image.
						setDoubleBuffered(false);
						paintComponent(totalGraphics);

						// Paint just the dragged tab to the buffer
						tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
						Graphics graphics = tabImage.getGraphics();
						graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y+bounds.height, TabPane.this);

						dragging = true;

						component = getComponentAt(draggedTabIndex);
						title = getTitleAt(draggedTabIndex);
						removeTabAt(draggedTabIndex);

						repaint();
					}
				} else {
					currentMouseLocation = e.getPoint();
					TabPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

					repaint();
				}
				super.mouseDragged(e);
			}

		});

	}
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(dragging && currentMouseLocation != null && tabImage != null) {
			g.drawImage(tabImage, currentMouseLocation.x-30, currentMouseLocation.y-10, this);
		}
	}

	public void setFlash(boolean on, int index){
		if(on) this.setForegroundAt(index, Color.red);
		else this.setForegroundAt(index, Color.black);
	}

}
