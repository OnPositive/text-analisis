package com.onpositive.semantic.wikipedia.abstracts.svc;

import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.output.MediaInfo;
import org.sweble.wikitext.engine.utils.UrlEncoding;
import org.sweble.wikitext.parser.nodes.WtUrl;

final class MyRendererCallback
			implements
				HtmlRendererCallback
	{
		protected static final String LOCAL_URL = "";
		
		@Override
		public boolean resourceExists(PageTitle target)
		{
			// TODO: Add proper check
			return false;
		}
		
		@Override
		public com.onpositive.semantic.wikipedia.abstracts.svc.MediaInfo getMediaInfo(String title, int width, int height) throws Exception
		{
			return null;
		}
		
		@Override
		public String makeUrl(PageTitle target)
		{
			String page = UrlEncoding.WIKI.encode(target.getNormalizedFullTitle());
			String f = target.getFragment();
			String url = page;
			if (f != null && !f.isEmpty())
				url = page + "#" + UrlEncoding.WIKI.encode(f);
			return LOCAL_URL + "/" + url;
		}
		
		@Override
		public String makeUrl(WtUrl target)
		{
			if (target.getProtocol() == "")
				return target.getPath();
			return target.getProtocol() + ":" + target.getPath();
		}
		
		@Override
		public String makeUrlMissingTarget(String path)
		{
			return LOCAL_URL + "?title=" + path + "&amp;action=edit&amp;redlink=1";
			
		}
	}