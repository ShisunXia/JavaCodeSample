package window;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.transform.Templates;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import part.Part;
import javax.swing.JTable;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TreeSelectionEvent;

public class Main {

	private JFrame frame;
	private Map<String, Part> namePartMapping;
	private Part top;
	private JTable table;
	private DefaultTableModel model;
	private JLabel parentChildPartContentLabel;
	private JLabel currentPartContentLabel;
	private JScrollPane scrollPaneTree;
	private JButton exitBtn;
	private JTree tree;
	private JButton populateDataBtn;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.out.println();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		try {
			
			constructTree();
			initializePartInfo();
			initializeView();
			initializeActionHandler();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * This method is used to add action handlers to GUI components
	 */
	private void initializeActionHandler() {
		
		//Handling tree selection action.
		//When a part is selected, the information of this part and its child parts will be updated to the table.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				if(node==null)
					return;
				//Retrieving the selected part
				String partName=(String)node.getUserObject();
				//Generating the table view.
				constructDatagrid(partName);
				//Updating "current Part" label
				currentPartContentLabel.setText(partName);
				currentPartContentLabel.setToolTipText(partName);
				//Updating "Parent Child Part" label
				if(namePartMapping.get(partName).getParent()!=null){
					parentChildPartContentLabel.setText(namePartMapping.get(partName).getParent().getName()+"\\"+partName);
				}else{
					parentChildPartContentLabel.setText(partName);
				}
				parentChildPartContentLabel.setToolTipText(parentChildPartContentLabel.getText());
				
			}

		});
		
		//Handling "Populate data" button clicking action.
		//Setting the visibility of tree to be true.
		populateDataBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollPaneTree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				tree.setVisible(true);
				populateDataBtn.setEnabled(false);
			}
		});
		
		//Handling "Exit program" button clicking action.
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	
	/**
	 * @throws IOException
	 * Parsing part.csv file and updating parts in namePartMapping.
	 */
	private void initializePartInfo() throws IOException {
		Reader in = new InputStreamReader(Main.class.getResourceAsStream("/csv/part.csv"));
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
		    String partName = record.get("NAME");
		    String partType = record.get("TYPE");
		    String partNumber = record.get("PART_NUMBER");
		    String partTitle = record.get("TITLE");
		    String partMaterial = record.get("MATERIAL");
		    String partItem=record.get("ITEM");
		    Part part;
		    // If namePartMapping does not contain this part then insert a new part object
		    if(namePartMapping.containsKey(partName)){
		    	part=namePartMapping.get(partName);
		    }else{
		    	part=new Part(partName);
		    	namePartMapping.put(partName, part);
		    }
		    //Updating parts information
		    part.setItem(partItem);
		    part.setMaterial(partMaterial);
		    part.setPartNumber(partNumber);
		    part.setTitle(partTitle);
		    part.setType(partType);
		}
	}

	/**
	 * @throws IOException
	 * Parsing bom.csv file and creating a map to store parts name and the parts object they associated with.
	 */
	private void constructTree() throws IOException {
		Reader in = new InputStreamReader(Main.class.getResourceAsStream("/csv/bom.csv"));
		namePartMapping = new HashMap<>();
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
		    String parentName = record.get("PARENT_NAME");
		    String componentName = record.get("COMPONENT_NAME");
		    int quantity=Integer.valueOf( record.get("QUANTITY"));
		    Part parent=null;
		    Part currentPart;
		    //If namePartMapping does not contain the parent part of current part, then create a parent part object.
		    if(!namePartMapping.containsKey(parentName)&&!parentName.equals("")){
		    	parent = new Part(parentName);
		    }else{
		    	parent=namePartMapping.get(parentName);
		    }
		    //If namePartMapping contains current part, then update parent and quantity. 
		    //Otherwise creating new part object and insert to namePartMapping
		    if(namePartMapping.containsKey(componentName)){
		    	currentPart=namePartMapping.get(componentName);
		    	currentPart.setParent(parent);
		    	currentPart.setQuantity(quantity);
		    }else{
		    	currentPart=new Part(componentName,parent,quantity);
		    	namePartMapping.put(componentName, currentPart);
		    }
		    //Storing parent part back to namePartMapping
		    if(parent!=null){
		    	parent.addChild(currentPart);
		    	namePartMapping.put(parentName, parent);
		    } else{
		    	top=currentPart;
		    }
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeView() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 600, 411);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//"Testing functionality of Tree and Datagrid" label
		JLabel label = new JLabel("Testing functionality of Tree and Datagrid");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		label.setBounds(99, 17, 329, 20);
		frame.getContentPane().add(label);

		JPanel panel = new JPanel();
		panel.setBounds(6, 51, 588, 165);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		//Creating a tree object and make it invisible
		DefaultMutableTreeNode topNode =constructTreeView(this.top);
		tree = new JTree(topNode);
		tree.setVisible(false);
		
		scrollPaneTree = new JScrollPane(tree,
			      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneTree.setBounds(47, 4, 279, 152);
		scrollPaneTree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		panel.add(scrollPaneTree);
		
		
		JLabel parentChildPartTxtLabel = new JLabel("Parent Child Part:");
		parentChildPartTxtLabel.setBounds(338, 7, 114, 16);
		panel.add(parentChildPartTxtLabel);
		
		parentChildPartContentLabel = new JLabel("");
		parentChildPartContentLabel.setBounds(464, 7, 103, 16);
		panel.add(parentChildPartContentLabel);
		
		
		JLabel currentPartTxtLabel = new JLabel("Current Part:");
		currentPartTxtLabel.setBounds(368, 35, 84, 16);
		panel.add(currentPartTxtLabel);
		
		currentPartContentLabel = new JLabel("");
		currentPartContentLabel.setBounds(464, 35, 103, 16);
		panel.add(currentPartContentLabel);
		
		//"Populate data in tree" button
		populateDataBtn = new JButton("Populate data in tree");
		populateDataBtn.setBounds(389, 63, 178, 29);
		panel.add(populateDataBtn);
		
		//"Exit from Application" button
		exitBtn = new JButton("Exit from Application");
		exitBtn.setBounds(389, 104, 178, 29);
		panel.add(exitBtn);
		
		//This panel is a container for the table component
		JPanel tablePanel = new JPanel();
		tablePanel.setBounds(6, 227, 588, 156);
		frame.getContentPane().add(tablePanel);
		tablePanel.setLayout(null);
		
		//Creating table components with column Names
		String[] columnNames = {"Parent Name","Component Name","Part Number","Title","Quantity","Type","Item","Material"};
		Object[][] data = {};
		model= new DefaultTableModel(data,columnNames);
		table = new JTable(model);
		table.setEnabled(false);
		JScrollPane scrollPaneData = new JScrollPane(table,
			      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			      JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPaneData.setBounds(6, 6, 576, 144);
		tablePanel.add(scrollPaneData);
	}

	/**
	 * @param Part
	 * @return DefaultMutableTreeNode
	 * This method is used to generate tree view of current part and its child parts.
	 */
	private DefaultMutableTreeNode constructTreeView(Part part) {
		Set<Part> childParts=part.getChild();
		Iterator<Part> childPartsIterator=childParts.iterator();
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(part.getName());
		List<Part> partsNoChild=new ArrayList<>();
		//Iterating all child parts. Ignoring single child part at first.
		while(childPartsIterator.hasNext()){
			Part childPart=childPartsIterator.next();
			//If this child part is single then put in partsNoChild Array. 
			//Otherwise recursively invoke construckTreeView(child part)
			if(childPart.getChild().size()>0){
				node.add(constructTreeView(childPart));
			}else{
				partsNoChild.add(childPart);
			}
		}
		//Inserting parts in partsNoChild list to the tree view.
		for(int i=0;i<partsNoChild.size();i++){
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(partsNoChild.get(i).getName());
			node.add(childNode);
		}
		return node;
	}
	
	/**
	 * @param partName
	 * This method is used to construct table view. 
	 * It update all information about partName and its child parts to the table.
	 */
	private void constructDatagrid(String partName) {
		Part part=namePartMapping.get(partName);
		Set<Part> childParts=part.getChild();
		Iterator<Part> childPartsIterator=childParts.iterator();
		//Clearing the table
		model.setRowCount(0);
		String parentName="";
		// Getting the name of parent part.
		if(part.getParent()!=null){
			parentName=part.getParent().getName();
		}
		Object[] rowData={parentName,part.getName(),part.getPartNumber(),part.getTitle(),
				part.getQuantity(),part.getType(),part.getItem(),part.getMaterial()};
		model.addRow(rowData);
		//Iterating through all child parts and inserting them to the table.
		while(childPartsIterator.hasNext()){
			Part childPart=childPartsIterator.next();
			Object[] rowDataChild={part.getName(),childPart.getName(),childPart.getPartNumber(),
					childPart.getTitle(),childPart.getQuantity(),childPart.getType(),childPart.getItem(),childPart.getMaterial()};
			model.addRow(rowDataChild);
		}
	}
}
