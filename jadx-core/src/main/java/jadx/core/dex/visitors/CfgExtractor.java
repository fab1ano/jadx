package jadx.core.dex.visitors;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import jadx.api.JavaMethod;
import jadx.core.dex.nodes.*;

import java.util.*;

public class CfgExtractor extends AbstractVisitor {
	private final List<MethodNode> filter = new ArrayList<>();
	private final Map<MethodNode,MutableGraph<BlockNode>> cfgCache = new HashMap<>();

	public CfgExtractor() {}

	public MutableGraph<BlockNode> getCfg(MethodNode methodNode) {
		return cfgCache.get(methodNode);
	}

	@Override
	public void visit(MethodNode mth) {
		if (mth.isNoCode()) {
			return;
		}

		if (!filter.contains(mth) || cfgCache.containsKey(mth)) {
			// Skip if not in filter or already created
			return;
		}

		if (mth.getBasicBlocks() != null) {
			System.out.println("Generating CFG for: " + mth);
			cfgCache.put(mth, generateIntraCfg(mth));
		}
	}

	public void setFilter(Set<JavaMethod> nodes) {
		for (JavaMethod javaMethod: nodes) {
			filter.add(javaMethod.getMethodNode());
		}
	}

	private static MutableGraph<BlockNode> generateIntraCfg(MethodNode mth) {
		List<BlockNode> basicBlocks = mth.getBasicBlocks();

		MutableGraph<BlockNode> intraCfg =
				GraphBuilder.directed().incidentEdgeOrder(ElementOrder.stable()).build();

		for (BlockNode basicBlock: basicBlocks) {
			intraCfg.addNode(basicBlock);
		}

		for (BlockNode basicBlock: basicBlocks) {
			intraCfg.addNode(basicBlock);

			for (BlockNode successor: basicBlock.getSuccessors()) {
				intraCfg.putEdge(basicBlock, successor);
			}
		}
		return intraCfg;
	}
}
