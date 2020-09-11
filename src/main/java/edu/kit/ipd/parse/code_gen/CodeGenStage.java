package edu.kit.ipd.parse.code_gen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import edu.kit.ipd.parse.luna.data.PostPipelineData;
import org.kohsuke.MetaInfServices;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.ipd.parse.luna.data.ast.visitor.IVisitor;
import edu.kit.ipd.parse.luna.data.AbstractPipelineData;
import edu.kit.ipd.parse.luna.data.PipelineDataCastException;
import edu.kit.ipd.parse.luna.pipeline.IPipelineStage;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

/**
 * @author Sebastian Weigelt
 * @author Viktor Kiesel
 */
@MetaInfServices(IPipelineStage.class)
public class CodeGenStage implements IPipelineStage {

	private final String ID = "CodeGenPipeStage";

	private PostPipelineData appd;

	private static final Logger logger = LoggerFactory.getLogger(CodeGenStage.class);
	private String visitor;

	@Override
	public void init() {
		Properties conf = ConfigManager.getConfiguration(CodeGenStage.class);
		visitor = (conf.getProperty("Visitor"));
	}

	@Override
	public void exec(AbstractPipelineData data) throws PipelineStageException {
		logger.debug("Starting Pipeline {}", ID);
		// casting data
		try {
			appd = data.asPostPipelineData();

			Reflections reflections = new Reflections("edu.kit.ipd.parse.code_gen");
			Set<Class<? extends edu.kit.ipd.parse.luna.data.ast.visitor.IVisitor>> classes = reflections
					.getSubTypesOf(edu.kit.ipd.parse.luna.data.ast.visitor.IVisitor.class).stream()
					.filter(e -> e.getPackageName().startsWith("edu.kit.ipd.parse.code_gen")).collect(Collectors.toSet());

			Set<IVisitor> visitorLoader = new HashSet<>();

			for (Class<? extends IVisitor> aClass : classes) {
				if (!Modifier.isAbstract(aClass.getModifiers())) {
					visitorLoader.add(aClass.getConstructor().newInstance());
				}

			}

			String code = null;
			for (IVisitor v : visitorLoader) {
				if (visitor.equals(v.getID())) {
					if (appd.isMethod()) {
						code = v.toNewMethod((appd.getAstRoot()), appd.getMethod());
					} else {
						code = v.toCode(appd.getAstRoot());
					}
				}
			}
			if (code == null) {
				throw new NullPointerException("No Visitor found!");
			}
			if (code.equals("")) {
				logger.error("No Code generated!");
			}
			appd.setCode(code);
		} catch (

		PipelineDataCastException e) {
			logger.error(e.toString());
			logger.info("Cannot process on data");
			return;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getID() {
		return ID;
	}

}
