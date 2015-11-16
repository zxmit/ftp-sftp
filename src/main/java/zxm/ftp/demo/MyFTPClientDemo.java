package zxm.ftp.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class MyFTPClientDemo {
	public void downAllFiles(FTPClient ftpClient, String remoteDir,
			String localDir) throws UnsupportedEncodingException, IOException {
		ftpClient.changeWorkingDirectory(remoteDir);
		FTPFile[] fs = ftpClient.listFiles();	
		
		for(FTPFile ff : fs) {
			if(ff.getType() == 0) {
				ftpClient.changeWorkingDirectory(remoteDir);
				System.out.println("download file " +remoteDir + ff.getName());
				downloadFile(ftpClient, ff.getName(), localDir);
			} else if(ff.getType() == 1) {
				String childRemoteDir = null;
				String childLocalDir = null;
				if(remoteDir.endsWith("/")) {
					childRemoteDir = remoteDir + ff.getName();
				}else {
					childRemoteDir = remoteDir + "/" + ff.getName();
				}
				if(localDir.endsWith("/")) {
					childLocalDir = localDir + ff.getName();
				}else {
					childLocalDir = localDir + "/" + ff.getName();
				}
				downAllFiles(ftpClient, childRemoteDir, childLocalDir);
			}
		}
	}
	
	public void downloadFile(FTPClient ftpClient, String remoteFile, String localPath) throws IOException {
		judgeStage(ftpClient);
		File path = new File(localPath);
		if(!path.exists()) {
			path.mkdirs();
		}
		
		File localFile = new File(path, remoteFile);
		if(!localFile.exists()) {
			OutputStream is = new FileOutputStream(localFile);
			System.out.println("downloading...");
			ftpClient.retrieveFile(remoteFile, is);
			System.out.println("downloaded");
			is.close();
		}
	}

	public FTPClient connect(String url, int port, String username,
			String password) throws IOException {
		FTPClient ftpClient = new FTPClient();
		int reply;
		ftpClient.setControlEncoding("GB2312");

		ftpClient.connect(url, port);

		ftpClient.login(username, password);//

		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

		reply = ftpClient.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			System.err.println("FTP server refused connection.");
			return null;
		}
		System.out.println("FTP server connected");
		return ftpClient;
	}

	public void judgeStage(FTPClient ftpClient) throws IOException {
		int reply = ftpClient.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			System.err.println("FTP server refused connection.");
		}
	}
	
	public boolean close(FTPClient ftpClient) throws IOException {
		if (ftpClient != null) {
			return ftpClient.logout();
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		MyFTPClientDemo mfc = new MyFTPClientDemo();
		FTPClient client = mfc.connect("172.16.1.39", 21, "zxm", "752369");
		mfc.downAllFiles(client, "/", "F:/tmp/ftp_tmp");
		mfc.close(client);
	}


}
