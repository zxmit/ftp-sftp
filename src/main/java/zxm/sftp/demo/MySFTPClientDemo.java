package zxm.sftp.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class MySFTPClientDemo {

	/**
	 * 创建ssh连接
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 */
	public Session connectSSH(String host, int port, String username,
			String password) {
		Session sshSession = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			sshSession = jsch.getSession(username, host, port);
			System.out.println("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sshSession;
	}
	
	/**
	 * 创建sftp连接
	 * @param sshSession
	 * @return
	 */
	public ChannelSftp connectSftp(Session sshSession) {
		ChannelSftp sftp = null;
		try {
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sftp;
	}

	/**
	 * 下载文件
	 * 
	 * @param directory
	 * @param downloadFile
	 * @param saveFile
	 * @param sftp
	 */
	public void download(String directory, String downloadFile,
			String saveFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(saveFile);
			sftp.get(downloadFile, new FileOutputStream(file));
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 罗列某个目录下的所有文件
	 * 
	 * @param directory
	 * @param sftp
	 * @return
	 * @throws SftpException
	 */
	public Vector listFiles(String directory, ChannelSftp sftp)
			throws SftpException {
		return sftp.ls(directory);
	}

	/**
	 * 下载某个目录下的所有文件，不进行递归遍历
	 * 
	 * @param directory
	 * @param saveDirectory
	 * @param sftp
	 * @throws SftpException
	 */
	public void download(String directory, String saveDirectory,
			ChannelSftp sftp) throws SftpException {
		Vector<LsEntry> vector = listFiles(directory, sftp);
		if (!vector.isEmpty()) {
			java.util.Iterator<LsEntry> it = vector.iterator();

			while (it.hasNext()) {
				LsEntry entry = it.next();
				String fileName = entry.getFilename();
				if (fileName.startsWith("."))
					continue;
				String downloadFile = directory + "/" + fileName;
				String saveFile = saveDirectory + "\\" + fileName;
				download(directory, downloadFile, saveFile, sftp);
			}
		}
	}

	/**
	 * 测试删除某个文件
	 * 
	 * @param directory
	 * @param deleteFile
	 * @param sftp
	 */
	public void delete(String directory, String deleteFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试下载某个文件
	 * 
	 * @param sf
	 * @param sftp
	 */
	public static void testDownloadFile(MySFTPClientDemo sf, ChannelSftp sftp) {
		String directory = "/home/liuzhe/sftp";
		String downloadFile = "camus.properties";
		String saveFile = "C:\\test\\camus.properties";
		sf.download(directory, downloadFile, saveFile, sftp);
	}

	/**
	 * 测试下载某个目录下的所有文件
	 * 
	 * @param sf
	 * @param sftp
	 * @throws SftpException
	 */
	public static void testDownloadPath(MySFTPClientDemo sf, ChannelSftp sftp)
			throws SftpException {
		String directory = "/home/liuzhe/sftp2";
		String saveDirectory = "F:\\sftp";
		sf.download(directory, saveDirectory, sftp);
	}

	public static void main(String[] args) {
		MySFTPClientDemo sf = new MySFTPClientDemo();
		String host = "172.16.8.103";
		int port = 22;
		String username = "liuzhe";
		String password = "111111";
		
		Session sshSession = sf.connectSSH(host, port, username, password);
		ChannelSftp sftp = sf.connectSftp(sshSession);
		
		try {
			 testDownloadFile(sf, sftp);

			 System.out.println("finished");

			 testDownloadPath(sf, sftp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(sftp != null) {
				sftp.disconnect();
			}
			if(sshSession != null) {
				sshSession.disconnect();
			}
		}
	}
}
