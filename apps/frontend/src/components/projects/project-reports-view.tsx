// project-reports-view.tsx
"use client";

import {
  Activity,
  BarChart3,
  Calendar,
  CheckCircle,
  Clock,
  Download,
  RefreshCw,
  Target,
  TrendingDown,
  TrendingUp,
} from "lucide-react";
import React, { useState } from "react";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ProjectDTO } from "@/types/projects";

interface ProjectReportsViewProps {
  project: ProjectDTO | null;
}

export default function ProjectReportsView({
  project,
}: ProjectReportsViewProps) {
  const [selectedPeriod, setSelectedPeriod] = useState("30d");
  const [isLoading, setIsLoading] = useState(false);

  // Mock data - replace with actual API calls
  const overviewMetrics = {
    totalTasks: 127,
    completedTasks: 89,
    inProgressTasks: 23,
    blockedTasks: 15,
    teamVelocity: 12.5,
    avgCompletionTime: 3.2,
    onTimeDelivery: 85,
    teamEfficiency: 92,
  };

  const velocityData = [
    { week: "Week 1", completed: 8, planned: 10 },
    { week: "Week 2", completed: 12, planned: 12 },
    { week: "Week 3", completed: 15, planned: 14 },
    { week: "Week 4", completed: 11, planned: 13 },
  ];

  const teamPerformance = [
    { name: "John Doe", tasksCompleted: 23, efficiency: 95, avatar: "JD" },
    { name: "Jane Smith", tasksCompleted: 18, efficiency: 88, avatar: "JS" },
    { name: "Mike Johnson", tasksCompleted: 21, efficiency: 92, avatar: "MJ" },
    { name: "Sarah Wilson", tasksCompleted: 16, efficiency: 87, avatar: "SW" },
  ];

  const taskBreakdown = [
    { status: "Completed", count: 89, percentage: 70, color: "bg-green-500" },
    { status: "In Progress", count: 23, percentage: 18, color: "bg-blue-500" },
    { status: "To Do", count: 15, percentage: 12, color: "bg-gray-400" },
  ];

  const handleRefresh = async () => {
    setIsLoading(true);
    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1000));
    setIsLoading(false);
  };

  const handleExport = () => {
    // Implementation for exporting reports
    console.log("Exporting reports...");
  };

  return (
    <div className="max-w-7xl mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold flex items-center gap-2">
            <BarChart3 className="w-6 h-6" />
            Project Reports
          </h2>
          <p className="text-muted-foreground">
            Analytics and insights for {project?.name}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
            <SelectTrigger className="w-32">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="7d">Last 7 days</SelectItem>
              <SelectItem value="30d">Last 30 days</SelectItem>
              <SelectItem value="90d">Last 3 months</SelectItem>
              <SelectItem value="1y">Last year</SelectItem>
            </SelectContent>
          </Select>

          <Button
            variant="outline"
            size="sm"
            onClick={handleRefresh}
            disabled={isLoading}
            className="flex items-center gap-2"
          >
            <RefreshCw
              className={`w-4 h-4 ${isLoading ? "animate-spin" : ""}`}
            />
            Refresh
          </Button>

          <Button
            variant="outline"
            size="sm"
            onClick={handleExport}
            className="flex items-center gap-2"
          >
            <Download className="w-4 h-4" />
            Export
          </Button>
        </div>
      </div>

      {/* Overview Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">
                  Total Tasks
                </p>
                <p className="text-2xl font-bold">
                  {overviewMetrics.totalTasks}
                </p>
              </div>
              <div className="p-2 bg-blue-100 rounded-lg">
                <Target className="w-6 h-6 text-blue-600" />
              </div>
            </div>
            <div className="mt-2 flex items-center text-sm">
              <TrendingUp className="w-4 h-4 text-green-500 mr-1" />
              <span className="text-green-600">+12%</span>
              <span className="text-muted-foreground ml-1">
                from last month
              </span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">
                  Completion Rate
                </p>
                <p className="text-2xl font-bold">
                  {Math.round(
                    (overviewMetrics.completedTasks /
                      overviewMetrics.totalTasks) *
                      100,
                  )}
                  %
                </p>
              </div>
              <div className="p-2 bg-green-100 rounded-lg">
                <CheckCircle className="w-6 h-6 text-green-600" />
              </div>
            </div>
            <div className="mt-2">
              <Progress
                value={
                  (overviewMetrics.completedTasks /
                    overviewMetrics.totalTasks) *
                  100
                }
                className="h-2"
              />
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">
                  Team Velocity
                </p>
                <p className="text-2xl font-bold">
                  {overviewMetrics.teamVelocity}
                </p>
              </div>
              <div className="p-2 bg-purple-100 rounded-lg">
                <Activity className="w-6 h-6 text-purple-600" />
              </div>
            </div>
            <div className="mt-2 flex items-center text-sm">
              <TrendingUp className="w-4 h-4 text-green-500 mr-1" />
              <span className="text-green-600">+8%</span>
              <span className="text-muted-foreground ml-1">tasks/week</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">
                  Avg. Completion Time
                </p>
                <p className="text-2xl font-bold">
                  {overviewMetrics.avgCompletionTime}d
                </p>
              </div>
              <div className="p-2 bg-orange-100 rounded-lg">
                <Clock className="w-6 h-6 text-orange-600" />
              </div>
            </div>
            <div className="mt-2 flex items-center text-sm">
              <TrendingDown className="w-4 h-4 text-green-500 mr-1" />
              <span className="text-green-600">-0.5d</span>
              <span className="text-muted-foreground ml-1">improved</span>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Detailed Reports */}
      <Tabs defaultValue="velocity" className="space-y-4">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="velocity">Velocity</TabsTrigger>
          <TabsTrigger value="team">Team Performance</TabsTrigger>
          <TabsTrigger value="breakdown">Task Breakdown</TabsTrigger>
          <TabsTrigger value="timeline">Timeline</TabsTrigger>
        </TabsList>

        {/* Velocity Chart */}
        <TabsContent value="velocity">
          <Card>
            <CardHeader>
              <CardTitle>Sprint Velocity</CardTitle>
              <CardDescription>
                Track your team's velocity over time
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="h-64 flex items-end justify-between bg-muted/30 rounded-lg p-4">
                  {velocityData.map((week, index) => (
                    <div
                      key={index}
                      className="flex flex-col items-center flex-1 max-w-16"
                    >
                      <div className="flex flex-col items-center gap-1 mb-2">
                        <div
                          className="w-8 bg-blue-500 rounded-t"
                          style={{ height: `${(week.completed / 16) * 120}px` }}
                        />
                        <div
                          className="w-8 bg-gray-300 rounded-t"
                          style={{ height: `${(week.planned / 16) * 120}px` }}
                        />
                      </div>
                      <div className="text-xs text-center">
                        <div className="font-medium">{week.week}</div>
                        <div className="text-muted-foreground">
                          {week.completed}/{week.planned}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="flex items-center justify-center gap-6 text-sm">
                  <div className="flex items-center gap-2">
                    <div className="w-3 h-3 bg-blue-500 rounded" />
                    <span>Completed</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="w-3 h-3 bg-gray-300 rounded" />
                    <span>Planned</span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Team Performance */}
        <TabsContent value="team">
          <Card>
            <CardHeader>
              <CardTitle>Team Performance</CardTitle>
              <CardDescription>
                Individual team member statistics and performance
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {teamPerformance.map((member, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between p-4 border rounded-lg"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center text-primary-foreground font-medium">
                        {member.avatar}
                      </div>
                      <div>
                        <p className="font-medium">{member.name}</p>
                        <p className="text-sm text-muted-foreground">
                          {member.tasksCompleted} tasks completed
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="text-right">
                        <p className="text-sm font-medium">
                          {member.efficiency}%
                        </p>
                        <p className="text-xs text-muted-foreground">
                          Efficiency
                        </p>
                      </div>
                      <div className="w-20">
                        <Progress value={member.efficiency} className="h-2" />
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Task Breakdown */}
        <TabsContent value="breakdown">
          <Card>
            <CardHeader>
              <CardTitle>Task Status Breakdown</CardTitle>
              <CardDescription>
                Distribution of tasks across different statuses
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {taskBreakdown.map((item, index) => (
                    <div
                      key={index}
                      className="text-center p-4 border rounded-lg"
                    >
                      <div
                        className={`w-16 h-16 ${item.color} rounded-full mx-auto mb-3 flex items-center justify-center`}
                      >
                        <span className="text-white font-bold text-lg">
                          {item.count}
                        </span>
                      </div>
                      <h3 className="font-medium">{item.status}</h3>
                      <p className="text-sm text-muted-foreground">
                        {item.percentage}% of total
                      </p>
                    </div>
                  ))}
                </div>

                <div className="space-y-3">
                  {taskBreakdown.map((item, index) => (
                    <div
                      key={index}
                      className="flex items-center justify-between"
                    >
                      <div className="flex items-center gap-3">
                        <div className={`w-4 h-4 ${item.color} rounded`} />
                        <span className="font-medium">{item.status}</span>
                      </div>
                      <div className="flex items-center gap-4">
                        <span className="text-sm text-muted-foreground">
                          {item.count} tasks
                        </span>
                        <div className="w-32">
                          <Progress value={item.percentage} className="h-2" />
                        </div>
                        <span className="text-sm font-medium w-8">
                          {item.percentage}%
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Timeline */}
        <TabsContent value="timeline">
          <Card>
            <CardHeader>
              <CardTitle>Project Timeline</CardTitle>
              <CardDescription>
                Project milestones and key deliverables
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="text-center py-8 text-muted-foreground">
                  <Calendar className="w-12 h-12 mx-auto mb-4 opacity-50" />
                  <p>Timeline view is coming soon</p>
                  <p className="text-sm">
                    Track project milestones and deadlines
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
