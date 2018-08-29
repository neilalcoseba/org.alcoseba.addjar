package org.alcoseba.addjarplugin.classpath;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;

public class AddJarClasspathVariableInitializer extends ClasspathVariableInitializer {

	public static final String JAR_DIRECTORIES = "JAR_DIRECTORIES";

	@Override
	public void initialize(String variable) {
		System.out.println("AddJarClasspathVariableInitializer : " + variable);

		try {
			File localRepositoryDir = new File(System.getProperty("user.home"), "Downloads");
			IPath newPath = new Path(localRepositoryDir.getAbsolutePath());
			JavaCore.setClasspathVariable(JAR_DIRECTORIES, newPath, new NullProgressMonitor());
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
	}
}
