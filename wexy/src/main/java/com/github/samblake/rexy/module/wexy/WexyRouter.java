package com.github.samblake.rexy.module.wexy;

import com.github.samblake.rexy.http.Method;
import com.github.samblake.rexy.module.wexy.actions.WexyAction;
import jauter.Router;

public class WexyRouter extends Router<Method, WexyAction, WexyRouter> {

	@Override protected WexyRouter getThis() { return this; }
	
	@Override protected Method CONNECT() { return Method.CONNECT; }
	@Override protected Method DELETE()  { return Method.DELETE;  }
	@Override protected Method GET()     { return Method.GET;     }
	@Override protected Method HEAD()    { return Method.HEAD;    }
	@Override protected Method OPTIONS() { return Method.OPTIONS; }
	@Override protected Method PATCH()   { return Method.PATCH;   }
	@Override protected Method POST()    { return Method.POST;    }
	@Override protected Method PUT()     { return Method.PUT;     }
	@Override protected Method TRACE()   { return Method.TRACE;   }
	
}