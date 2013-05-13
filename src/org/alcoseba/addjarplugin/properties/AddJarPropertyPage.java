package org.alcoseba.addjarplugin.properties;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class AddJarPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
	public AddJarPropertyPage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite branchesComposite = new Composite(parent, SWT.NONE);
		branchesComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		Label lblBranches = new Label(branchesComposite, SWT.NONE);
		lblBranches.setText("Branches");

		File downloadDir = new File("/home/neil/Downloads");
		final File[] directories = downloadDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});

		ComboViewer branchesComboViewer = new ComboViewer(branchesComposite, SWT.NONE | SWT.READ_ONLY);
		branchesComboViewer.setSelection(new StructuredSelection(directories[0]));
		branchesComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		branchesComboViewer.setInput(directories);

		final Combo branchesCombo = branchesComboViewer.getCombo();
		branchesCombo.setLayoutData(new RowData(500, SWT.DEFAULT));
		branchesCombo.select(0);

		branchesComboViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof File)) {
					return (null);
				}

				File file = (File) element;

				return (file.getName());
			}
		});

		final Composite tblJarFilesComposite = new Composite(parent, SWT.NONE);

		final TableViewer tblJarFilesViewer = new TableViewer(tblJarFilesComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
		tblJarFilesViewer.setContentProvider(ArrayContentProvider.getInstance());
		Table tblJarFiles = tblJarFilesViewer.getTable();
		tblJarFiles.setHeaderVisible(true);
		tblJarFiles.setLinesVisible(true);
		tblJarFiles.setBounds(0, 0, 500, 299);

		TableLayout tblJarFilesLayout = new TableLayout();
		tblJarFilesLayout.addColumnData(new ColumnPixelData(100));
		tblJarFilesLayout.addColumnData(new ColumnPixelData(100));
		
		TableViewerColumn tblViewerJarFilesSubSystemColumn = new TableViewerColumn(tblJarFilesViewer, SWT.NONE);
		TableColumn tblJarFileSubsystemCol = tblViewerJarFilesSubSystemColumn.getColumn();
		tblJarFileSubsystemCol.setWidth(193);
		tblJarFileSubsystemCol.setText("SubSystem");
		tblViewerJarFilesSubSystemColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof File)) {
					return (null);
				}

				File file = (File) element;

				return (file.getName());
			}
		});
		
		TableViewerColumn tblViewerJarFilesColumn = new TableViewerColumn(tblJarFilesViewer, SWT.NONE);
		TableColumn tblJarFilesCol = tblViewerJarFilesColumn.getColumn();
		tblJarFilesCol.setWidth(50);
		tblJarFilesCol.setText("Jar Files");
		tblViewerJarFilesColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof File)) {
					return (null);
				}

				File file = (File) element;

				return (file.getPath());
			}
		});
		
		File[] subDirectories = getAllSubDirectories(directories[0]);
		tblJarFilesViewer.setInput(subDirectories);
		tblJarFilesViewer.setSelection(new StructuredSelection(directories[0]));
		
		branchesCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File directory = directories[branchesCombo.getSelectionIndex()];
				System.out.println("Retrieving sub directories");
				File[] sub = getAllSubDirectories(directory);
				System.out.println("Sub Directory Lenght : "+sub.length);
				System.out.println("End retrieving sub directories");
				tblJarFilesViewer.setInput(sub);
			}
		});
		
		return parent;
	}

	public File[] getAllSubDirectories(File subDirectory) {
		List<File> directories = new ArrayList<File>(0);
		List<File> subDirectories = new ArrayList<File>(0);
		subDirectories.add(subDirectory);

		do {
			subDirectory = subDirectories.remove(0);
			File[] locatedDirectories = subDirectory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return new File(dir, name).isDirectory();
				}
			});

			directories.addAll(Arrays.asList(locatedDirectories));
			subDirectories.addAll(Arrays.asList(locatedDirectories));
		} while (subDirectories.size() != 0);

		return (directories.toArray(new File[0]));
	}
}
