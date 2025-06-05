"use client";

import "@xyflow/react/dist/style.css";

import dagre from "@dagrejs/dagre";
import {
  addEdge,
  Background,
  Connection,
  ConnectionLineType,
  Controls,
  Edge,
  Node,
  Panel,
  ReactFlow,
  ReactFlowProvider,
  useEdgesState,
  useNodesState,
} from "@xyflow/react";
import React, { useCallback, useEffect, useState } from "react";

import { WorkflowDetailDTO } from "@/types/workflows";

const nodeWidth = 172;
const nodeHeight = 36;

const dagreGraph = new dagre.graphlib.Graph().setDefaultEdgeLabel(() => ({}));

const getLayoutElements = (
  nodes: Node[],
  edges: Edge[],
  direction: "TB" | "LR" = "TB",
): { nodes: Node[]; edges: Edge[] } => {
  dagreGraph.setGraph({ rankdir: direction });

  nodes.forEach((node) => {
    dagreGraph.setNode(node.id, { width: nodeWidth, height: nodeHeight });
  });

  edges.forEach((edge) => {
    dagreGraph.setEdge(edge.source, edge.target);
  });

  dagre.layout(dagreGraph);

  const newNodes = nodes.map((node) => {
    const nodeWithPosition = dagreGraph.node(node.id);
    return {
      ...node,
      position: {
        x: nodeWithPosition.x - nodeWidth / 2,
        y: nodeWithPosition.y - nodeHeight / 2,
      },
    };
  });

  return { nodes: newNodes, edges };
};

const convertStatesToNodes = (workflowDetails: WorkflowDetailDTO): Node[] => {
  return workflowDetails.states.map((state) => ({
    id: state.id!.toString(),
    data: { label: state.stateName },
    style: {
      backgroundColor: state.isInitial
        ? "var(--initial-node-bg)" // Initial node background
        : state.isFinal
          ? "var(--final-node-bg)" // Final node background
          : "var(--intermediate-node-bg)", // Intermediate node background
      color: "var(--node-text-color)", // Text color
      border: "1px solid var(--node-border-color)", // Border color
      borderRadius: "6px",
      padding: "6px 12px",
      fontSize: "0.875rem",
      fontWeight: "500",
    },
    position: { x: 0, y: 0 },
    type: "default",
  }));
};

const convertTransitionsToEdges = (
  workflowDetails: WorkflowDetailDTO,
): Edge[] => {
  return workflowDetails.transitions
    .filter(
      (transition) =>
        transition.sourceStateId !== null &&
        transition.targetStateId !== null &&
        workflowDetails.states.some(
          (state) => state.id === transition.sourceStateId,
        ) &&
        workflowDetails.states.some(
          (state) => state.id === transition.targetStateId,
        ),
    ) // Ensure source and target states exist
    .map((transition, index) => ({
      id: `e${transition.sourceStateId}-${transition.targetStateId}-${index}`, // Add index to ensure uniqueness
      source: transition.sourceStateId!.toString(),
      target: transition.targetStateId!.toString(),
      label: transition.eventName,
      labelStyle: {
        fill: "var(--edge-label-color)",
        fontWeight: "500",
        fontSize: "0.75rem",
      },
      labelBgStyle: {
        fill: "var(--edge-label-bg)",
        rx: 4,
        ry: 4,
      },
      labelBgPadding: [4, 4],
      type: ConnectionLineType.SmoothStep,
      animated: true,
      style: {
        stroke: "var(--edge-color)", // Edge color
        strokeWidth: 2,
      },
    }));
};

// Main Flow Component
export const WorkflowDiagram: React.FC<{
  workflowDetails: WorkflowDetailDTO;
}> = ({ workflowDetails }) => {
  const [theme, setTheme] = useState(
    document.documentElement.classList.contains("dark") ? "dark" : "light",
  );
  const [nodes, setNodes, onNodesChange] = useNodesState([] as Node[]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([] as Edge[]);
  const [diagramHeight, setDiagramHeight] = useState("40rem");
  const [diagramDirection, setDiagramDirection] = useState<"TB" | "LR">("TB");

  useEffect(() => {
    // Observe changes in the dark class
    const observer = new MutationObserver(() => {
      setTheme(
        document.documentElement.classList.contains("dark") ? "dark" : "light",
      );
    });

    observer.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ["class"],
    });

    return () => observer.disconnect();
  }, []);

  // Re-layout the graph when direction changes
  useEffect(() => {
    if (nodes.length > 0 && edges.length > 0) {
      const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutElements(
        nodes,
        edges,
        diagramDirection,
      );
      setNodes(layoutedNodes);
      setEdges(layoutedEdges);
    }
  }, [diagramDirection]);

  useEffect(() => {
    function initNodesAndEdges() {
      const initialNodes = convertStatesToNodes(workflowDetails);
      const initialEdges = convertTransitionsToEdges(workflowDetails);

      // Calculate optimal height based on number of nodes
      const nodeCount = initialNodes.length;
      let calculatedHeight = "40rem"; // Default height

      // Adjust height based on node count
      if (nodeCount > 20) {
        calculatedHeight = "60rem";
      } else if (nodeCount > 10) {
        calculatedHeight = "50rem";
      }

      // Switch to horizontal layout if there are many nodes in a sequence
      const shouldUseHorizontalLayout =
        nodeCount > 8 && initialEdges.length < nodeCount * 1.5;
      const direction = shouldUseHorizontalLayout ? "LR" : "TB";

      const { nodes: layoutedNodes, edges: layoutedEdges } = getLayoutElements(
        initialNodes,
        initialEdges,
        direction,
      );

      setDiagramDirection(direction);
      setDiagramHeight(calculatedHeight);
      setNodes(layoutedNodes);
      setEdges(layoutedEdges);
    }
    initNodesAndEdges();
  }, [workflowDetails, setNodes, setEdges]);

  const onConnect = useCallback(
    (params: Connection) =>
      setEdges((eds) =>
        addEdge(
          { ...params, type: ConnectionLineType.SmoothStep, animated: true },
          eds,
        ),
      ),
    [setEdges],
  );

  const styles =
    theme === "dark"
      ? {
          "--initial-node-bg": "#81C784",
          "--final-node-bg": "#FF8A65",
          "--intermediate-node-bg": "#64B5F6",
          "--node-text-color": "#F9FAFB",
          "--node-border-color": "#4B5563",
          "--edge-color": "#94A3B8",
          "--edge-label-color": "#F9FAFB",
          "--edge-label-bg": "#334155",
          "--background-color": "#1E293B",
          "--grid-color": "#374151",
        }
      : {
          "--initial-node-bg": "#4CAF50",
          "--final-node-bg": "#FF5722",
          "--intermediate-node-bg": "#2196F3",
          "--node-text-color": "#FFFFFF",
          "--node-border-color": "#D1D5DB",
          "--edge-color": "#64748B",
          "--edge-label-color": "#1E293B",
          "--edge-label-bg": "#F1F5F9",
          "--background-color": "#F8FAFC",
          "--grid-color": "#E2E8F0",
        };

  return (
    <ReactFlowProvider>
      <div
        className="workflow-container w-full"
        style={{
          ...styles,
          backgroundColor: "var(--background-color)",
          height: diagramHeight,
        }}
      >
        <ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          onConnect={onConnect}
          connectionLineType={ConnectionLineType.SmoothStep}
          fitView
          fitViewOptions={{ padding: 0.2 }}
          minZoom={0.1}
          maxZoom={1.5}
          defaultViewport={{ x: 0, y: 0, zoom: 0.8 }}
          attributionPosition="bottom-right"
        >
          <Background
            gap={16}
            size={1}
            color="var(--grid-color)" // Grid color
          />
          <Controls position="top-right" showInteractive={false} />
          <Panel
            position="top-left"
            className="bg-background/80 p-2 rounded-md shadow-xs backdrop-blur-xs"
          >
            <div className="flex flex-col gap-2">
              <div className="flex items-center gap-3 text-xs">
                <div className="flex items-center">
                  <div
                    className="w-3 h-3 rounded-sm mr-1"
                    style={{ backgroundColor: "var(--initial-node-bg)" }}
                  ></div>
                  <span>Initial</span>
                </div>
                <div className="flex items-center">
                  <div
                    className="w-3 h-3 rounded-sm mr-1"
                    style={{ backgroundColor: "var(--intermediate-node-bg)" }}
                  ></div>
                  <span>Intermediate</span>
                </div>
                <div className="flex items-center">
                  <div
                    className="w-3 h-3 rounded-sm mr-1"
                    style={{ backgroundColor: "var(--final-node-bg)" }}
                  ></div>
                  <span>Final</span>
                </div>
              </div>

              <div className="flex items-center gap-2 text-xs">
                <button
                  onClick={() =>
                    setDiagramDirection(diagramDirection === "TB" ? "LR" : "TB")
                  }
                  className="px-2 py-1 bg-background border border-border rounded-md text-xs hover:bg-accent"
                >
                  {diagramDirection === "TB"
                    ? "Horizontal Layout"
                    : "Vertical Layout"}
                </button>
              </div>
            </div>
          </Panel>
        </ReactFlow>
      </div>
    </ReactFlowProvider>
  );
};
