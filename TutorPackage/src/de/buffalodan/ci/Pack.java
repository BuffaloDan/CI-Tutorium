package de.buffalodan.ci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Pack {

	private static final FilenameFilter JAVA_FILTER = (dir, name) -> {
		return name.endsWith(".java");
	};
	
	private static final FilenameFilter JAR_FILTER = (dir, name) -> {
		return name.endsWith(".jar") && !name.equals("CI-Library.jar");
	};

	private static void processJavaFile(File f) throws IOException {
		String fName = f.getName();
		File folder = f.getParentFile();
		File ftmp = new File(folder, fName + "_old");
		f.renameTo(ftmp);
		f = ftmp;
		File newFile = new File(folder, fName);
		Scanner fileScanner = new Scanner(f);
		String s = "";
		while (!s.startsWith("package")) {
			s = fileScanner.nextLine();
		}

		FileWriter fileStream = new FileWriter(newFile);
		BufferedWriter out = new BufferedWriter(fileStream);
		while (fileScanner.hasNextLine()) {
			String next = fileScanner.nextLine();
			if (next.startsWith("import de.buffalodan"))
				continue;
			if (next.equals("\n"))
				out.newLine();
			else
				out.write(next);
			out.newLine();
		}
		fileScanner.close();
		out.close();
		f.delete();
	}

	private static void copyJavaFiles(File folder, File dest) throws IOException {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				copyJavaFiles(f, dest);
			} else {
				if (f.getName().endsWith(".java")) {
					FileUtils.copyFileToDirectory(f, dest);
				}
			}
		}
	}

	public static void compressZipfile(File sourceDir, File outputFile) throws IOException, FileNotFoundException {
		ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
		compressDirectoryToZipfile(sourceDir, zipFile);
		IOUtils.closeQuietly(zipFile);
	}

	private static void compressDirectoryToZipfile(File rootDir, ZipOutputStream out)
			throws IOException, FileNotFoundException {
		for (File file : rootDir.listFiles()) {
			ZipEntry entry = new ZipEntry(file.getName());
			out.putNextEntry(entry);

			FileInputStream in = new FileInputStream(file);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
		}
	}
	
	private static void createBuildAndRunScript(File libFolder, File tmpFolder) throws IOException {
		String buildBat = "javac *.java -cp \"";
		String buildSh = "javac *.java -cp \"";
		String runBat = "java -cp \"";
		String runSh = "java -cp \"";
		
		boolean f = true;
		for (String lib:libFolder.list(JAR_FILTER)) {
			if (f) f=false;
			else {
				buildBat += ";";
				buildSh += ":";
				runBat += ";";
				runSh += ":";
			}
			buildBat += lib;
			buildSh += lib;
			runBat += lib;
			runSh += lib;
		}
		buildBat += "\"";
		buildSh += "\"";
		runBat += ";.\" Main %1 %2";
		runSh += ":.\" Main $1 $2";
		FileWriter fw = new FileWriter(new File(tmpFolder, "build.bat"));
		FileWriter fw2 = new FileWriter(new File(tmpFolder, "build.sh"));
		FileWriter fw3 = new FileWriter(new File(tmpFolder, "run.bat"));
		FileWriter fw4 = new FileWriter(new File(tmpFolder, "run.sh"));
		fw.write(buildBat);
		fw2.write(buildSh);
		fw3.write(runBat);
		fw4.write(runSh);
		fw.close();
		fw2.close();
		fw3.close();
		fw4.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: TutorPackage.jar <ProjectFolder>");
			return;
		}
		File projectFolder = new File(args[0]);
		File srcFolder = new File(projectFolder, "src");
		File tmpFolder = new File("../tmp");
		File libFolder = new File("../lib");
		File ciLibSrcFolder = new File("../CI-Library/src");
		if (!tmpFolder.exists())
			tmpFolder.mkdir();

		// Copy Libs
		for (File f : libFolder.listFiles(JAR_FILTER)) {
			FileUtils.copyFileToDirectory(f, tmpFolder);
		}

		// Copy Java Files
		copyJavaFiles(ciLibSrcFolder, tmpFolder);
		copyJavaFiles(srcFolder, tmpFolder);

		// Remove Package-Tag
		for (File f : tmpFolder.listFiles(JAVA_FILTER)) {
			processJavaFile(f);
		}
		
		createBuildAndRunScript(libFolder, tmpFolder);

		// Zip
		compressZipfile(tmpFolder, new File(projectFolder, "tutor-src.zip"));
		FileUtils.deleteDirectory(tmpFolder);
	}

}
