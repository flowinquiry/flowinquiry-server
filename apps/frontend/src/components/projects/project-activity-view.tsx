"use client";

import {
  Activity,
  CheckCircle,
  Clock,
  Filter,
  GitCommit,
  MessageSquare,
  Search,
  Settings,
  Users,
} from "lucide-react";
import React, { useState } from "react";

import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ProjectDTO } from "@/types/projects";

interface ProjectActivityViewProps {
  project: ProjectDTO | null;
}

interface ActivityItem {
  id: string;
  type: "task" | "comment" | "team" | "integration" | "system";
  action: string;
  user: {
    name: string;
    avatar: string;
  };
  timestamp: string;
  details?: string;
  metadata?: {
    taskId?: string;
    taskTitle?: string;
    oldValue?: string;
    newValue?: string;
  };
}

export default function ProjectActivityView({
  project,
}: ProjectActivityViewProps) {
  const [filter, setFilter] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedPeriod, setSelectedPeriod] = useState("7d");

  // Mock activity data - replace with actual API calls
  const activities: ActivityItem[] = [
    {
      id: "1",
      type: "task",
      action: "moved task to Done",
      user: { name: "John Doe", avatar: "JD" },
      timestamp: "2 hours ago",
      details: "Task 'Implement user authentication' completed",
      metadata: { taskId: "T-123", taskTitle: "Implement user authentication" },
    },
    {
      id: "2",
      type: "comment",
      action: "commented on task",
      user: { name: "Jane Smith", avatar: "JS" },
      timestamp: "3 hours ago",
      details: "Great work on the API integration! Ready for testing.",
      metadata: { taskId: "T-456", taskTitle: "API Integration" },
    },
    {
      id: "3",
      type: "team",
      action: "added new team member",
      user: { name: "Mike Johnson", avatar: "MJ" },
      timestamp: "4 hours ago",
      details: "Sarah Wilson joined the project as a Developer",
      metadata: { newValue: "Sarah Wilson" },
    },
    {
      id: "4",
      type: "task",
      action: "created new task",
      user: { name: "Sarah Wilson", avatar: "SW" },
      timestamp: "5 hours ago",
      details: "Task 'Database optimization' created in To Do",
      metadata: { taskId: "T-789", taskTitle: "Database optimization" },
    },
    {
      id: "5",
      type: "integration",
      action: "connected GitHub repository",
      user: { name: "System", avatar: "SY" },
      timestamp: "6 hours ago",
      details: "Repository 'project-repo' connected successfully",
    },
    {
      id: "6",
      type: "task",
      action: "updated task priority",
      user: { name: "John Doe", avatar: "JD" },
      timestamp: "1 day ago",
      details: "Changed priority from Medium to High",
      metadata: {
        taskId: "T-101",
        taskTitle: "Security audit",
        oldValue: "Medium",
        newValue: "High",
      },
    },
    {
      id: "7",
      type: "system",
      action: "project milestone reached",
      user: { name: "System", avatar: "SY" },
      timestamp: "2 days ago",
      details: "75% of Sprint 3 tasks completed",
    },
    {
      id: "8",
      type: "comment",
      action: "mentioned you in a comment",
      user: { name: "Jane Smith", avatar: "JS" },
      timestamp: "2 days ago",
      details: "@john can you review the latest changes?",
      metadata: { taskId: "T-202", taskTitle: "Code review" },
    },
  ];

  const getActivityIcon = (type: string) => {
    switch (type) {
      case "task":
        return CheckCircle;
      case "comment":
        return MessageSquare;
      case "team":
        return Users;
      case "integration":
        return GitCommit;
      case "system":
        return Settings;
      default:
        return Activity;
    }
  };

  const getActivityColor = (type: string) => {
    switch (type) {
      case "task":
        return "text-blue-600 bg-blue-100";
      case "comment":
        return "text-green-600 bg-green-100";
      case "team":
        return "text-purple-600 bg-purple-100";
      case "integration":
        return "text-orange-600 bg-orange-100";
      case "system":
        return "text-gray-600 bg-gray-100";
      default:
        return "text-blue-600 bg-blue-100";
    }
  };

  const filteredActivities = activities.filter((activity) => {
    const matchesFilter = filter === "all" || activity.type === filter;
    const matchesSearch =
      searchQuery === "" ||
      activity.action.toLowerCase().includes(searchQuery.toLowerCase()) ||
      activity.details?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      activity.user.name.toLowerCase().includes(searchQuery.toLowerCase());

    return matchesFilter && matchesSearch;
  });

  const activityStats = {
    totalActivities: activities.length,
    tasksUpdated: activities.filter((a) => a.type === "task").length,
    commentsAdded: activities.filter((a) => a.type === "comment").length,
    teamChanges: activities.filter((a) => a.type === "team").length,
  };

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold flex items-center gap-2">
            <Activity className="w-6 h-6" />
            Project Activity
          </h2>
          <p className="text-muted-foreground">
            Recent activity and updates for {project?.name}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
            <SelectTrigger className="w-32">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="1d">Today</SelectItem>
              <SelectItem value="7d">Last 7 days</SelectItem>
              <SelectItem value="30d">Last 30 days</SelectItem>
              <SelectItem value="all">All time</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Activity Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-2">
              <Activity className="w-5 h-5 text-blue-600" />
              <div>
                <p className="text-sm text-muted-foreground">Total Activity</p>
                <p className="text-xl font-bold">
                  {activityStats.totalActivities}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-2">
              <CheckCircle className="w-5 h-5 text-green-600" />
              <div>
                <p className="text-sm text-muted-foreground">Task Updates</p>
                <p className="text-xl font-bold">
                  {activityStats.tasksUpdated}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-2">
              <MessageSquare className="w-5 h-5 text-purple-600" />
              <div>
                <p className="text-sm text-muted-foreground">Comments</p>
                <p className="text-xl font-bold">
                  {activityStats.commentsAdded}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center gap-2">
              <Users className="w-5 h-5 text-orange-600" />
              <div>
                <p className="text-sm text-muted-foreground">Team Changes</p>
                <p className="text-xl font-bold">{activityStats.teamChanges}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Filters and Search */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            <Input
              placeholder="Search activity..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>

        <Select value={filter} onValueChange={setFilter}>
          <SelectTrigger className="w-48">
            <Filter className="w-4 h-4 mr-2" />
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Activity</SelectItem>
            <SelectItem value="task">Task Updates</SelectItem>
            <SelectItem value="comment">Comments</SelectItem>
            <SelectItem value="team">Team Changes</SelectItem>
            <SelectItem value="integration">Integrations</SelectItem>
            <SelectItem value="system">System Events</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Activity Feed */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span>Activity Feed</span>
            <Badge variant="outline">{filteredActivities.length} items</Badge>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {filteredActivities.length > 0 ? (
              filteredActivities.map((activity) => {
                const Icon = getActivityIcon(activity.type);
                const colorClass = getActivityColor(activity.type);

                return (
                  <div
                    key={activity.id}
                    className="flex items-start gap-4 p-4 rounded-lg border hover:bg-muted/50 transition-colors"
                  >
                    <div className={`p-2 rounded-lg ${colorClass}`}>
                      <Icon className="w-4 h-4" />
                    </div>

                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-1">
                        <Avatar className="w-6 h-6">
                          <AvatarFallback className="text-xs">
                            {activity.user.avatar}
                          </AvatarFallback>
                        </Avatar>
                        <span className="font-medium text-sm">
                          {activity.user.name}
                        </span>
                        <span className="text-sm text-muted-foreground">
                          {activity.action}
                        </span>
                        <Badge variant="outline" className="text-xs">
                          {activity.type}
                        </Badge>
                      </div>

                      {activity.details && (
                        <p className="text-sm text-muted-foreground mb-2">
                          {activity.details}
                        </p>
                      )}

                      {activity.metadata?.taskTitle && (
                        <div className="flex items-center gap-2 text-xs text-muted-foreground">
                          <span>Task:</span>
                          <Badge variant="secondary" className="text-xs">
                            {activity.metadata.taskId}
                          </Badge>
                          <span>{activity.metadata.taskTitle}</span>
                        </div>
                      )}
                    </div>

                    <div className="flex items-center gap-2 text-xs text-muted-foreground">
                      <Clock className="w-3 h-3" />
                      <span>{activity.timestamp}</span>
                    </div>
                  </div>
                );
              })
            ) : (
              <div className="text-center py-8 text-muted-foreground">
                <Activity className="w-12 h-12 mx-auto mb-4 opacity-50" />
                <p>No activity found</p>
                <p className="text-sm">
                  Try adjusting your filters or search terms
                </p>
              </div>
            )}
          </div>

          {filteredActivities.length > 0 && (
            <div className="flex justify-center mt-6">
              <Button variant="outline">Load More Activity</Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
