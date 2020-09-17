package edu.kit.ipd.parse.code_gen;

import edu.kit.ipd.parse.luna.data.PostPipelineData;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import org.junit.Ignore;
import org.junit.Test;

public class CodeGenTest {

	@Ignore
	@Test
	public void testStart() {
		System.out.println("Test");
		CodeGenStage codeGenStage = new CodeGenStage();
		codeGenStage.init();
		try {
			codeGenStage.exec(new PostPipelineData());
		} catch (PipelineStageException e) {
			e.printStackTrace();
		}
	}

}
