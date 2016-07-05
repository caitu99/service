package com.caitu99.service.utils.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.sys.domain.IpInfo;

public class IPpool {
	public static Map<String, List<Node>> ips = new HashMap<>();
	public static Map<String, Integer> curNode = new HashMap<>();
	public static Map<String, Integer> curIpResource = new HashMap<>();
	public static Pattern pattern = Pattern.compile("IPCallBack\\(([^\\(]*)\\)");
	public static final String PUBLICKEY = "ucloudweb@caitu99.com14417047760001979738875";
	public static final String PRIVATEKEY = "89ed6b537c7b810e8d0c48b7ec71bf0e17260226";
	
	
	
	public static void init(List<IpInfo> ipInfos) {
		ips.clear();
		if(ipInfos == null) return ;
		for(IpInfo ipInfo : ipInfos) {
			String cityCode = ipInfo.getCityCode();
			String hostId = ipInfo.getHostId();
			String ip = ipInfo.getIp();
			String eipId = ipInfo.getEipId();
			String ipRegon = ipInfo.getRegon();
			Long id = ipInfo.getId();
			//拿到该区域的所有节点
			List<Node> regon = ips.get(cityCode);
			if(regon == null) {
				regon = new ArrayList<>();
				ips.put(cityCode, regon);
				//设置该区域的当前节点为0指针
				curNode.put(cityCode, 0);
			}
			Node node = getNodeFromRegon(regon, hostId);
			if(node == null) {
				node = new Node();
				node.setCityCode(cityCode);
				node.setHostId(hostId);
				//设置该节点当前ip为0指针
				curIpResource.put(hostId, 0);
				regon.add(node);
			}
			List<IpResource> ipResources = node.getIpResources();
			if(ipResources == null) {
				ipResources = new ArrayList<>();
				node.setIpResources(ipResources);
			}
			IpResource ipResource = new IpResource(id, ipRegon, ip, eipId);
			ipResources.add(ipResource);
		}
		System.out.println("代理信息加载完毕");
	}
	
	private static Node getNodeFromRegon(List<Node> regon, String hostId) {
		for(Node node : regon) {
			if(node.getHostId().equals(hostId)) {
				return node;
			}
		}
		return null;
	}

	public static String getCityCodeByIP(String ip) throws Exception {
		URL url = new URL("http://whois.pconline.com.cn/ipJson.jsp?ip=" + ip);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.connect();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gbk"));
		String store = null;
		StringBuffer sb = new StringBuffer();
		while((store = br.readLine()) != null) {
			sb.append(store).append("\n");
		}
		br.close();
		conn.disconnect();
		String result = sb.toString().trim();
		Matcher matcher = pattern.matcher(result);
		if(matcher.find()) {
			result = matcher.group(1);
		}
		return (String) JSON.parseObject(result).get("cityCode");
	}
}
