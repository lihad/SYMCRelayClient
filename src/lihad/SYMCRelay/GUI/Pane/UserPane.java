package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tree.WebTree;
import com.alee.laf.tree.WebTreeCellRenderer;
import com.alee.laf.tree.WebTreeModel;

import lihad.SYMCRelay.Channel;
import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.ActionAdapter;
import lihad.SYMCRelay.GUI.RotatedButton;

public class UserPane extends WebPanel {

	private static final long serialVersionUID = -8395492876472798137L;
	private WebPanel userPane;
	private WebTree<DefaultMutableTreeNode> webTree;
	private DefaultMutableTreeNode top = new DefaultMutableTreeNode("SYMCRelay User List");

	public void expandChannel(String string){
		for(int i = 0; i < top.getChildCount(); i++){
			if(top.getChildAt(i).toString().replaceFirst("#", "").substring(0, top.getChildAt(i).toString().replaceFirst("#", "").lastIndexOf("_")).equalsIgnoreCase(string)){
				webTree.expandNode((DefaultMutableTreeNode) top.getChildAt(i));
			}else{
				webTree.collapsePath(new TreePath(((DefaultMutableTreeNode) top.getChildAt(i)).getPath()));
			}
		}
	}
	
	public void updateNodes(String string) {		
		DefaultMutableTreeNode channel = null;
		DefaultMutableTreeNode user = null;
		
		top.removeAllChildren();

		String[] elements = string.split("\n");
		for(int i = 0; i<elements.length; i++){
			if(elements[i].contains("#")){
				channel = new DefaultMutableTreeNode(elements[i]);
				try{Client.getChannel(channel.getUserObject().toString().replaceFirst("#", "").substring(0, channel.getUserObject().toString().replaceFirst("#", "").lastIndexOf("_"))).unsync_userlist.clear();}catch(NullPointerException e){
					Client.logger.debug("UserPane just tried to update a channel user list and got it wrong: "+channel.getUserObject().toString().replaceFirst("#", "").substring(0, channel.getUserObject().toString().replaceFirst("#", "").lastIndexOf("_")));
				};
				top.add(channel);
			}else{
				if(channel != null){
					user = new DefaultMutableTreeNode(elements[i]);
					channel.add(user);
					if(Client.getChannel(channel.getUserObject().toString().replaceFirst("#", "").substring(0, channel.getUserObject().toString().replaceFirst("#", "").lastIndexOf("_"))) != null){
						Client.getChannel(channel.getUserObject().toString().replaceFirst("#", "").substring(0, channel.getUserObject().toString().replaceFirst("#", "").lastIndexOf("_"))).unsync_userlist.add(elements[i]);
					}else{
						Client.logger.debug("UserPane just tried to update a channel user list and got it wrong: "+channel.getUserObject().toString().replaceFirst("#", "").substring(0, channel.getUserObject().toString().replaceFirst("#", "").lastIndexOf("_")));
					}
				}
			}
		}
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> enu = top.children();
		while(enu.hasMoreElements()){
			sortchildrenA(enu.nextElement());
		}
		@SuppressWarnings("rawtypes")
		WebTreeModel model = (WebTreeModel) (webTree.getModel()); 
		model.reload(); 
		webTree.expandNode(top);
		if(Client.gui.tabbedPane.getSelectedIndex() >= 0) expandChannel(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getSelectedIndex()).replaceFirst("#", ""));
	}
	
	public void sortchildrenA(DefaultMutableTreeNode node){
		@SuppressWarnings("unchecked")
		ArrayList<DefaultMutableTreeNode> children = Collections.list(node.children());
        // for getting original location
        ArrayList<String> orgCnames = new ArrayList<String>();
        // new location
        ArrayList<String> cNames = new ArrayList<String>();
        //move the child to here so we can move them back
        DefaultMutableTreeNode temParent = new DefaultMutableTreeNode();
        for(Object child:children) {
            DefaultMutableTreeNode ch = (DefaultMutableTreeNode)child;
            temParent.insert(ch,0);
            cNames.add(ch.toString().toUpperCase());
            orgCnames.add(ch.toString().toUpperCase());
        }
        Collections.sort(cNames);
        for(String name:cNames) {
            // find the original location to get from children arrayList
            int indx = orgCnames.indexOf(name);
            node.insert(children.get(indx),node.getChildCount());
        }
	}


	public UserPane(){
		super(new BorderLayout());
		userPane = new WebPanel(new BorderLayout());

		webTree = new WebTree<DefaultMutableTreeNode>(top);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setOpenIcon(null);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setClosedIcon(null);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setLeafIcon(null);
		((WebTreeCellRenderer) webTree.getCellRenderer()).setRootIcon(null);


		WebScrollPane scrollPane = new WebScrollPane(webTree);
		scrollPane.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		userPane.add(scrollPane, BorderLayout.CENTER);
		userPane.setPreferredSize(new Dimension(175, 200));
		userPane.setVisible(Client.getRelayConfiguration().getUserListExpanded());
		scrollPane.getVerticalScrollBar().setUnitIncrement(32);



		final RotatedButton expandButton = new RotatedButton("show user list", false);
		final WebButton shrinkButton = new WebButton("hide user list");

		shrinkButton.setVisible(Client.getRelayConfiguration().getUserListExpanded());
		expandButton.setVisible(!Client.getRelayConfiguration().getUserListExpanded());

		ActionAdapter expandButtonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				expandButton.setVisible(false);
				shrinkButton.setVisible(true);
				userPane.setVisible(true);
				Client.getRelayConfiguration().setUserListExpanded(true);
			}
		};
		ActionAdapter shrinkButtonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				expandButton.setVisible(true);
				shrinkButton.setVisible(false);
				userPane.setVisible(false);
				Client.getRelayConfiguration().setUserListExpanded(false);
			}
		};

		expandButton.addActionListener(expandButtonListener);
		shrinkButton.addActionListener(shrinkButtonListener);

		this.add(userPane, BorderLayout.EAST);
		this.add(expandButton, BorderLayout.WEST);
		this.add(shrinkButton, BorderLayout.SOUTH);
	}
}