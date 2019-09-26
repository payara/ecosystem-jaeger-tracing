/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 *  Copyright (c) [2019] Payara Foundation and/or its affiliates. All rights reserved.
 * 
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License.  You can
 *  obtain a copy of the License at
 *  https://github.com/payara/Payara/blob/master/LICENSE.txt
 *  See the License for the specific
 *  language governing permissions and limitations under the License.
 * 
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License.
 * 
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/legal/LICENSE.txt.
 * 
 *  GPL Classpath Exception:
 *  The Payara Foundation designates this particular file as subject to the "Classpath"
 *  exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *  file that accompanied this code.
 * 
 *  Modifications:
 *  If applicable, add the following below the License Header, with the fields
 *  enclosed by brackets [] replaced by your own identifying information:
 *  "Portions Copyright [year] [name of copyright owner]"
 * 
 *  Contributor(s):
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 */
package fish.payara.jaeger.tracer.lib;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper for the Jaeger tracer to allow it to be loaded as a service in Payara
 *
 * @author jonathan coustick
 */
public class JaegerTracerWrapper implements io.opentracing.Tracer {

    private static Tracer wrappedTracer;

    /**
     * Public constructor that can be called to initialise instance by serviceloader.
     */
    public JaegerTracerWrapper() {
        Logger.getLogger("JAEGER_WRAPPER").log(Level.SEVERE, "Jaeger Tracer Wrapper created");
        setUpTracer();   
    }

    private synchronized void setUpTracer() {
        if (wrappedTracer == null) {
            Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType(ConstSampler.TYPE).withParam(1);
            Configuration.SenderConfiguration senderConfig = Configuration.SenderConfiguration.fromEnv().withAgentHost("localhost").withAgentPort(6831);
            Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
            Configuration configuration = Configuration.fromEnv("jaeger-test").withSampler(samplerConfig).withReporter(reporterConfig);
            wrappedTracer = configuration.getTracer();
            Logger.getLogger("JAEGER_WRAPPER").log(Level.SEVERE, "Sending using " + configuration.getReporter().getSenderConfiguration().getSender().getClass().getCanonicalName());
            GlobalTracer.register(wrappedTracer);
        }
    }
    
    @Override
    public ScopeManager scopeManager() {
        return GlobalTracer.get().scopeManager();
    }

    @Override
    public Span activeSpan() {
        return GlobalTracer.get().activeSpan();
    }

    @Override
    public SpanBuilder buildSpan(String string) {
        return GlobalTracer.get().buildSpan(string);
    }

    @Override
    public <C> void inject(SpanContext sc, Format<C> format, C c) {
        GlobalTracer.get().inject(sc, format, c);
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C c) {
        return GlobalTracer.get().extract(format, c);
    }

}
