package org.alcoseba.addjarplugin.properties;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

public class AddJarPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
	private TableViewer tblJarFilesViewer;

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

		this.tblJarFilesViewer = new TableViewer(tblJarFilesComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
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
				File[] jarFiles = file.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".jar");
					}
				});

				StringBuilder buff = new StringBuilder();
				int jarFilesLength = jarFiles.length;

				for (int i = 0; i < jarFilesLength; i++) {
					File jarFile = jarFiles[i];

					if ((i + 1) == jarFilesLength) {
						buff.append(jarFile.getName());
					} else {
						buff.append(jarFile.getName() + ",");
					}
				}

				return (buff.toString());
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
				System.out.println("Sub Directory Lenght : " + sub.length);
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

	@Override
	protected void performApply() {
		System.out.println("APPLY");
		super.performApply();
		TableItem[] tableItems = this.tblJarFilesViewer.getTable().getItems();
		List<File> jarFiles = new ArrayList<File>(0);

		for (TableItem tableItem : tableItems) {
			if (!tableItem.getChecked()) {
				continue;
			}

			Object obj = tableItem.getData();

			if (!(obj instanceof File)) {
				continue;
			}

			File file = (File) obj;
			File[] dirJarFiles = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});
			
			jarFiles.addAll(Arrays.asList(dirJarFiles));
		}
		
		setClasspath(jarFiles.toArray(new File[0]));
	}

	@Override
	public boolean performOk() {
		return super.performOk();
	}

	public void setClasspath(File[] jarFiles) {
		System.out.println("Classpath Jar : "+jarFiles);
		IProject project = null;
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();

		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof IResource) {
				System.out.println("IResource");
				project = ((IResource) element).getProject();
				// TODO : PUT HERE IN ADDING JAR FILES ON A "RESOURCE PAGE"
			} else if (element instanceof IPackageFragmentRoot) {
				System.out.println("IPackageFragmentRoot");
				IJavaProject jProject = ((IPackageFragmentRoot) element).getJavaProject();
				project = jProject.getProject();
			} else if (element instanceof IJavaElement) {
				System.out.println("IJavaElement");
				IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

				for (File jarFile : jarFiles) {
					entries.add(JavaCore.newLibraryEntry(new Path(jarFile.getAbsolutePath()), null, new Path("/")));
				}

				try {
					IClasspathEntry[] classPathEntries = jProject.getRawClasspath();
					entries.addAll(Arrays.asList(classPathEntries));

					jProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}

				project = jProject.getProject();
			}
		}
	}
}
