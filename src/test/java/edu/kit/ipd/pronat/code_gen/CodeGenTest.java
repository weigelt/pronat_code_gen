package edu.kit.ipd.pronat.code_gen;

import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.pronat.postpipelinedatamodel.PostPipelineData;
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
