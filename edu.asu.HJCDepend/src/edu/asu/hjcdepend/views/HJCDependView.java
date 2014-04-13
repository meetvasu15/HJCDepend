package edu.asu.hjcdepend.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.views.markers.MarkerSupportInternalUtilities;
import org.eclipse.ui.part.ViewPart;

import edu.asu.Constants;
import edu.asu.Executor;
import edu.asu.Util;
import edu.asu.hjcdepend.ResultStoreBean;
import edu.asu.hjcdepend.CustomException.IllegalFileException;

/**
 * 
 * This view genrates the plug-in panel for HJCDepend
 */

public class HJCDependView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.asu.hjcdepend.views.HJCDependView";
	static Logger log = Logger
			.getLogger("edu.asu.hjcdepend.views.HJCDependView");
	private TableViewer viewer;
	private static List<ResultStoreBean> allIssuesFoundList;

	/**
	 * The constructor.
	 */
	public HJCDependView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		GridLayout outerLayout = new GridLayout(2, false);
		parent.setLayout(outerLayout);
		// Run button
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Run HJC Depend");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Shell shell = parent.getShell();
				initializeAnalysis(); // this method is the entry point of the
				generateLogs();
				updateViewerContent();// dependency analysis core
			}

		});
		button.setLayoutData(new GridData());
		// Clear button
		Button buttonClr = new Button(parent, SWT.PUSH);
		buttonClr.setText("Clear");
		buttonClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// clearReportedBugs(); // this method is the entry point of the
				viewer.getTable().removeAll();
			}

		});
		buttonClr.setLayoutData(new GridData());
		createViewer(parent);
	}

	private void generateLogs() {
		for (ResultStoreBean oneIssue : allIssuesFoundList) {
			log.info(oneIssue.getType() + " reported of severity '"
					+ oneIssue.getSeverity() + "'. Description: "
					+ oneIssue.getDescription());
		}
		log.info("+++++++++++++++++++++++++++++++++++++++++++Analysis End++++++++++++++++++++++++++++++");

	}

	/*
	 * private void clearReportedBugs() { // TODO Auto-generated method stub
	 * allIssuesFoundList = new ArrayList<ResultStoreBean>();
	 * 
	 * 
	 * }
	 */

	private void initializeAnalysis() {
		try {
			allIssuesFoundList = new ArrayList<ResultStoreBean>();
			// allIssuesFoundList.clear();
			String[] prjctNhtmlPath = getCurrentFilePath();
			Executor exe = new Executor();
			allIssuesFoundList = exe.runDependencyAnalyser(prjctNhtmlPath);
		} catch (IllegalFileException e) {
			e.printStackTrace();
			allIssuesFoundList.add(new ResultStoreBean(Constants.ERROR,
					Constants.HJC_ERROR, Constants.CURRENT_FILE_NOT_HTML, null,
					null));
		}catch (Exception e) {
			e.printStackTrace();
			 
		}

	}

	/*
	 * This method get the path of the current open file and checks whether it
	 * is an HTML file or not returns - path of the HTML file
	 * 
	 * @return an array of 2 strings first is disk path of the project second is
	 * the disk path of the HTML File
	 */
	private String[] getCurrentFilePath() throws IllegalFileException {
		// code from
		// http://stackoverflow.com/questions/299619/get-the-absolute-path-of-the-currently-edited-file-in-eclipse#answer-9299638
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		IFile currFile = (IFile) workbenchPart.getSite().getPage()
				.getActiveEditor().getEditorInput().getAdapter(IFile.class);
		String[] path = new String[2];
		path[0] = currFile.getProject().getLocationURI().getPath();
		path[1] = currFile.getRawLocation().toString();
		// System.out.println("currFile.getProject().getFullPath().toString() "+currFile.getProject().getFullPath().toString());
		// System.out.println("currFile.getRawLocation().toString() " + path);
		String fileExtension = currFile.getFileExtension();
		if (!fileExtension.equalsIgnoreCase("html")) {
			throw new IllegalFileException(
					"Please run the plugin when the HTML file to analyze is in view.");
		}
		// System.out.println(fileExtension);
		// System.out.println("path: " + path);
		return path;
	}

	// copied from
	// http://www.vogella.com/tutorials/EclipseJFaceTable/article.html#jfacetable_viewer
	// is it legal?
	private void updateViewerContent() {
		viewer.getTable().removeAll();
		viewer.setInput(allIssuesFoundList);
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		attachDoubleClickListener(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		// get the content for the viewer, setInput will call getElements in the
		// contentProvider

		viewer.setInput(allIssuesFoundList);

		// make the selection available to other views
		getSite().setSelectionProvider(viewer);
		// set the sorter for the table

		// define layout for the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	private void attachDoubleClickListener(final Table table) {
		table.addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TableItem[] selection = table.getSelection();

				if (selection.length != 1) {
					return;
				}

				TableItem item = table.getSelection()[0];

				for (int i = 0; i < table.getColumnCount(); i++) {
					if (item != null) {
						Object rowClickedData = item.getData();
						if (rowClickedData != null
								&& rowClickedData instanceof ResultStoreBean) {
							ResultStoreBean clickedResult = (ResultStoreBean) rowClickedData;
							if (clickedResult != null
									&& clickedResult.getFileName() != null
									&& !Util.isBlankString(clickedResult
											.getFileName())
									&& clickedResult.getLineNo() != null
									&& Util.isInteger(clickedResult.getLineNo())) {
								goToLine(clickedResult.getFileName(), Integer
										.parseInt(clickedResult.getLineNo()));
								//System.out.println("Filename" + clickedResult.getFileName() + " line No" + clickedResult.getLineNo());
							}
						}
					}
					//System.out.println(item);
				}
			}

		});
	}

	/*
	 * This method takes the ciursor to the specified line number
	 * http://www.eclipsezone.com/eclipse/forums/m92221730.html
	 * http://wiki.eclipse
	 * .org/FAQ_How_do_I_open_an_editor_on_a_file_in_the_workspace%3F
	 * https://www
	 * .google.com/search?q=eclipse+open+file+in+editor+programmatically
	 * &oq=open+
	 * file+in+editor+eclipse+program&aqs=chrome.1.69i57j0.10561j0j1&sourceid
	 * =chrome&ie=UTF-8 http://wiki.eclipse.org/
	 * FAQ_How_do_I_open_an_editor_on_a_file_in_the_workspace%3F
	 * http://wiki.eclipse.org/FAQ_How_do_I_open_an_editor_programmatically%3F
	 */
	public void goToLine(String filePath, int lineNumber) {
		//String workspaceRelativeFilePath = Util.getWorkspaceRelativePath(filePath);
		try{
			if(filePath != null && !Util.isBlankString(filePath)){
				Path path = new Path(filePath);
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if(file.exists()){
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					HashMap map = new HashMap();
					map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
					IMarker marker;
					try {
						marker = file.createMarker(IMarker.TEXT);
			
						marker.setAttributes(map);
						IDE.openEditor(page, marker); // 3.0 API
						//marker.setAttribute(IMarker.MESSAGE, "A sample marker message");
					     // marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH); 
					 
					      //Marker .createMarker(root, attribs, IMarker.PROBLEM);
					 if(marker.exists())
						marker.delete();
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){
			//could not go to line
		}
	}
	
	

	public TableViewer getViewer() {
		return viewer;
	}

	// create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Severity", "Dependency Type", "Description",
				"File Name", "Line No." };
		int[] bounds = { 100, 100, 100, 100, 100 };

		// first column is for the Severity
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultStoreBean r = (ResultStoreBean) element;
				return r.getSeverity();
			}
		});

		// second column is for the type
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultStoreBean r = (ResultStoreBean) element;
				return r.getType();
			}
		});

		// now the description
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultStoreBean r = (ResultStoreBean) element;
				return r.getDescription();
			}
		});

		// now the File Name
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultStoreBean r = (ResultStoreBean) element;
				return r.getFileName();
			}

		});

		// now the line number
		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ResultStoreBean r = (ResultStoreBean) element;
				return r.getLineNo();
			}

		});

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}