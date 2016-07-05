/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.utils.file.CommonImgCodeApi;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: CommonImgCodeApiTest 
 * @author ws
 * @date 2015年12月19日 下午1:59:50 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class CommonImgCodeApiTest extends AbstractJunit{

	@Test
	public void test() {
		String base64EncodedStr = "iVBORw0KGgoAAAANSUhEUgAAAFcAAAAoCAIAAAAE3vEvAAAEcUlEQVRoge2Yb0xbVRTAf6+Uf+XPZhgoBWEZ8sHFZcbAdEsMGrNlMSJ2GxqY4OZQF53ZFha3WAxZbJPNmGzRxBCJuLFsRIFUzGKWEXWLZhjwgwsxEhcmgwEDxLCyyp9J64c+HtC+19K+V8oYv7wP59177j33nZx77jtXKGt1cd+jUzO4tbZQq3UsMKO2OLdgK92CSi9sKK1XM7zlmXy/Oobyd9SYUCLB5HALptqLgLC8Iwg0FmJ0ZMXzgpEP11GdS3UuBWmKymnl+4JeVkdZki9brx4KemaJGx+US3JgsWBdR0rMnJbzfTT1ql9SmG2pygtBML4/d4EtyvKbtUCSE98+oNd29tQYKh8jQgD4eYjTXQAJej5aj14H0FTfdr4PUxrPG+Vn+ORP2m9ruygZHjc3SbL9s5OBxYK5nTfaeLNNUaF/nB8GRHnTKoyxAHkpogtGJrl4SzNbQdB8KFm2Xfsd0dTL7UkAnUC+EQHypk3bepl0+hprv0uXQ/MVzbD54yHZdo13BDDhpOEme9YAPPEAW1NZGQXQ7eDK36KOrRfbdJ7b8hCFDwM4XdT8xeh/mq/IPyHJjr8Mc20UQCewLV1srO+R0cw0YJo+/74f4PdQZoQ4wazUFaoz4qvuOa9XR+gY9dSJ1vFWlpgyuh003gzRWmaIE8zux6Nd+x3hJjthzuvAuIxOyWqSYwDGp/i8k6npH5fUw8ldeme0dVh25it5RzddrgxiSQ6XVZIlR7gb5xULpywHx97f71vnwuFsSY7Xk28EcE5/2HMPiueFxMYknkwS5bpuBiZmuvqPDym5AFByQdOLsbLtsjhcVvfjDo15xcKuihN+dbYevybJ29Mx6AEuDZJh4JEEIgRKV3PsD1EhOZriTFFuHZ7Jmmoo+HYMSMlLHrwsfxDI4o6FAP6g169kX7Zib811WoYBMgyY16ITcLqoaCcxkiOPijpnb3BpEAHMa8mMk5+nsYcLt+Zlq25jVFHL5DwX7xtfO6K55gjwtSndh443xZnoBICrIwxN0HmHtn/Erm3prIgkXq/ogoDQygX49sLm148BL9sCyN1PJZEVL8rf9YtCQw8TUwCxEezMlB8YUnRFZb4Vlu8XYOFrSm25U31Uk3mWYwHu9VjQisXihbO1a8JoXcYL9iqFC5BQsrP0+kKaW7E7wy24/x3vr7xw90xxZMk5vOqIYLzQbdJl2HzelvjE+O5LfZ9+E/TwoJldSs6urAjjGfFlYeru+n7/eirwrqA9Pl5iKewIpesTpW/2JlT3C37p3J6U1ahYPssy+mxFwo8W6dVjb6tB6Orwc8trseeotLH4mdkRDa/F7jg95q1Rkfjr7Ncl6ZTA8sJsj1jsOS5rjGCWu0vTgn93HTCcOhmiyT2Ylxfq9m4oqmr1aJQ8Et7o+Mm242lTA3DmYGXJiSCLK7VnhNsX9/o2UVtHWOw5FnuOR+4IO64vVil1Re0VvBu1qaYWWywIexTvcyerXM3vveKpvwT+mtSzWCrr8LLsBYD/AVjsYe3oO5y0AAAAAElFTkSuQmCC";
        
		String imgCode = CommonImgCodeApi.recognizeImgCodeFromStr(base64EncodedStr);
		System.out.println(imgCode);
	}

}
