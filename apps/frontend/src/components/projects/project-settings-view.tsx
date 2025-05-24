// project-settings-view.tsx
"use client";

import {
  AlertCircle,
  Bell,
  CheckCircle,
  Clock,
  Edit,
  GitBranch,
  Plus,
  Save,
  Settings,
  Shield,
  Trash2,
  Users,
  Workflow,
} from "lucide-react";
import React, {useState } from "react";

import { Alert, AlertDescription } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Switch } from "@/components/ui/switch";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Textarea } from "@/components/ui/textarea";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { ProjectDTO } from "@/types/projects";

interface ProjectSettingsViewProps {
  project: ProjectDTO | null;
  onProjectUpdate?: (updatedProject: ProjectDTO) => void;
}

export default function ProjectSettingsView({
  project,
  onProjectUpdate,
}: ProjectSettingsViewProps) {
  const t = useAppClientTranslations();
  const [activeTab, setActiveTab] = useState("general");
  const [unsavedChanges, setUnsavedChanges] = useState<string[]>([]);
  const [isSaving, setIsSaving] = useState(false);
  const [lastSaved, setLastSaved] = useState<Date | null>(null);

  // Form states
  const [formData, setFormData] = useState({
    name: project?.name || "",
    description: project?.description || "",
    status: project?.status || "Active",
    startDate: project?.startDate || "",
    endDate: project?.endDate || "",
  });

  // Settings states
  const [settings, setSettings] = useState({
    notifications: {
      emailUpdates: true,
      slackIntegration: false,
      taskAssignments: true,
      deadlineReminders: true,
    },
    privacy: {
      isPublic: false,
      allowGuestAccess: false,
      requireApproval: true,
    },
    automation: {
      autoAssignTasks: false,
      autoMoveCompleted: true,
      syncWithCalendar: false,
    },
  });

  const settingsTabs = [
    {
      id: "general",
      label: "General",
      icon: Settings,
      description: "Basic project information and settings",
    },
    {
      id: "members",
      label: "Members",
      icon: Users,
      description: "Manage project team members and permissions",
    },
    {
      id: "workflow",
      label: "Workflow",
      icon: Workflow,
      description: "Configure workflow states and transitions",
    },
    {
      id: "integrations",
      label: "Integrations",
      icon: GitBranch,
      description: "Connect external tools and services",
    },
    {
      id: "notifications",
      label: "Notifications",
      icon: Bell,
      description: "Email and notification preferences",
    },
    {
      id: "security",
      label: "Security",
      icon: Shield,
      description: "Access control and security settings",
    },
  ];

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (!unsavedChanges.includes(field)) {
      setUnsavedChanges((prev) => [...prev, field]);
    }
  };

  const handleSettingChange = (
    category: string,
    field: string,
    value: boolean,
  ) => {
    setSettings((prev) => ({
      ...prev,
      [category]: {
        ...prev[category as keyof typeof prev],
        [field]: value,
      },
    }));
    const changeKey = `${category}.${field}`;
    if (!unsavedChanges.includes(changeKey)) {
      setUnsavedChanges((prev) => [...prev, changeKey]);
    }
  };

  const handleSave = async () => {
    setIsSaving(true);
    try {
      // Simulate API call
      await new Promise((resolve) => setTimeout(resolve, 1000));

      const updatedProject = { ...project!, ...formData };
      onProjectUpdate?.(updatedProject);

      setUnsavedChanges([]);
      setLastSaved(new Date());
    } catch (error) {
      console.error("Failed to save settings:", error);
    } finally {
      setIsSaving(false);
    }
  };

  const handleDiscard = () => {
    setFormData({
      name: project?.name || "",
      description: project?.description || "",
      status: project?.status || "Active",
      startDate: project?.startDate || "",
      endDate: project?.endDate || "",
    });
    setUnsavedChanges([]);
  };

  // Mock data
  const teamMembers = [
    {
      id: 1,
      name: "John Doe",
      email: "john@example.com",
      role: "Project Manager",
      avatar: "JD",
    },
    {
      id: 2,
      name: "Jane Smith",
      email: "jane@example.com",
      role: "Developer",
      avatar: "JS",
    },
    {
      id: 3,
      name: "Mike Johnson",
      email: "mike@example.com",
      role: "Designer",
      avatar: "MJ",
    },
  ];

  const integrations = [
    {
      id: "github",
      name: "GitHub",
      description: "Link commits and pull requests to tasks",
      connected: true,
      lastSync: "2 hours ago",
    },
    {
      id: "slack",
      name: "Slack",
      description: "Get notifications in your Slack channels",
      connected: false,
      lastSync: null,
    },
    {
      id: "jira",
      name: "Jira",
      description: "Sync with Jira issues and projects",
      connected: false,
      lastSync: null,
    },
    {
      id: "discord",
      name: "Discord",
      description: "Team communication and updates",
      connected: true,
      lastSync: "1 day ago",
    },
  ];

  return (
    <div className="max-w-6xl mx-auto p-6">
      {/* Header with Save Actions */}
      {unsavedChanges.length > 0 && (
        <Alert className="mb-6 border-yellow-200 bg-yellow-50">
          <AlertCircle className="h-4 w-4 text-yellow-600" />
          <AlertDescription className="flex items-center justify-between">
            <span>You have {unsavedChanges.length} unsaved change(s)</span>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={handleDiscard}
                disabled={isSaving}
              >
                Discard
              </Button>
              <Button
                size="sm"
                onClick={handleSave}
                disabled={isSaving}
                className="flex items-center gap-2"
              >
                {isSaving ? (
                  <>
                    <Clock className="w-4 h-4 animate-spin" />
                    Saving...
                  </>
                ) : (
                  <>
                    <Save className="w-4 h-4" />
                    Save Changes
                  </>
                )}
              </Button>
            </div>
          </AlertDescription>
        </Alert>
      )}

      {/* Last Saved Indicator */}
      {lastSaved && unsavedChanges.length === 0 && (
        <div className="mb-6 flex items-center gap-2 text-sm text-muted-foreground">
          <CheckCircle className="w-4 h-4 text-green-500" />
          <span>Last saved {lastSaved.toLocaleTimeString()}</span>
        </div>
      )}

      {/* Settings Navigation */}
      <Tabs
        value={activeTab}
        onValueChange={setActiveTab}
        className="space-y-6"
      >
        <TabsList className="grid w-full grid-cols-3 lg:grid-cols-6">
          {settingsTabs.map((tab) => {
            const Icon = tab.icon;
            return (
              <TabsTrigger
                key={tab.id}
                value={tab.id}
                className="flex items-center gap-2 text-xs"
              >
                <Icon className="w-4 h-4" />
                <span className="hidden sm:inline">{tab.label}</span>
              </TabsTrigger>
            );
          })}
        </TabsList>

        {/* General Settings */}
        <TabsContent value="general" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Project Information</CardTitle>
              <CardDescription>
                Basic details about your project
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label htmlFor="project-name">Project Name</Label>
                  <Input
                    id="project-name"
                    value={formData.name}
                    onChange={(e) => handleInputChange("name", e.target.value)}
                    placeholder="Enter project name"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="project-status">Status</Label>
                  <select
                    id="project-status"
                    value={formData.status}
                    onChange={(e) =>
                      handleInputChange("status", e.target.value)
                    }
                    className="w-full px-3 py-2 border border-input rounded-md bg-background"
                  >
                    <option value="Active">Active</option>
                    <option value="On Hold">On Hold</option>
                    <option value="Completed">Completed</option>
                    <option value="Archived">Archived</option>
                  </select>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="project-description">Description</Label>
                <Textarea
                  id="project-description"
                  value={formData.description}
                  onChange={(e) =>
                    handleInputChange("description", e.target.value)
                  }
                  placeholder="Describe your project"
                  rows={4}
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label htmlFor="start-date">Start Date</Label>
                  <Input
                    id="start-date"
                    type="date"
                    value={formData.startDate}
                    onChange={(e) =>
                      handleInputChange("startDate", e.target.value)
                    }
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="end-date">End Date</Label>
                  <Input
                    id="end-date"
                    type="date"
                    value={formData.endDate}
                    onChange={(e) =>
                      handleInputChange("endDate", e.target.value)
                    }
                  />
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Team Members */}
        <TabsContent value="members" className="space-y-6">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Team Members</CardTitle>
                  <CardDescription>
                    Manage project team members and their permissions
                  </CardDescription>
                </div>
                <Button className="flex items-center gap-2">
                  <Plus className="w-4 h-4" />
                  Invite Member
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {teamMembers.map((member) => (
                  <div
                    key={member.id}
                    className="flex items-center justify-between p-4 border rounded-lg hover:bg-muted/50 transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center text-primary-foreground font-medium">
                        {member.avatar}
                      </div>
                      <div>
                        <p className="font-medium">{member.name}</p>
                        <p className="text-sm text-muted-foreground">
                          {member.email}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-3">
                      <Badge variant="outline">{member.role}</Badge>
                      <Button variant="outline" size="sm">
                        <Edit className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Workflow */}
        <TabsContent value="workflow" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Workflow Configuration</CardTitle>
              <CardDescription>
                Configure workflow states, transitions, and automation rules
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {["To Do", "In Progress", "Review", "Done"].map(
                    (state, index) => (
                      <div
                        key={state}
                        className="flex items-center justify-between p-4 border rounded-lg"
                      >
                        <div className="flex items-center gap-3">
                          <div
                            className={`w-3 h-3 rounded-full ${
                              index === 0
                                ? "bg-gray-400"
                                : index === 1
                                  ? "bg-blue-500"
                                  : index === 2
                                    ? "bg-yellow-500"
                                    : "bg-green-500"
                            }`}
                          />
                          <span className="font-medium">{state}</span>
                        </div>
                        <Button variant="outline" size="sm">
                          Configure
                        </Button>
                      </div>
                    ),
                  )}
                </div>

                <Separator />

                <div className="space-y-4">
                  <h4 className="font-medium">Automation Settings</h4>
                  <div className="space-y-3">
                    {Object.entries(settings.automation).map(([key, value]) => (
                      <div
                        key={key}
                        className="flex items-center justify-between"
                      >
                        <div>
                          <Label htmlFor={key} className="text-sm font-medium">
                            {key
                              .replace(/([A-Z])/g, " $1")
                              .replace(/^./, (str) => str.toUpperCase())}
                          </Label>
                          <p className="text-xs text-muted-foreground">
                            {key === "autoAssignTasks" &&
                              "Automatically assign tasks to team members"}
                            {key === "autoMoveCompleted" &&
                              "Move completed tasks to Done column"}
                            {key === "syncWithCalendar" &&
                              "Sync project deadlines with calendar"}
                          </p>
                        </div>
                        <Switch
                          id={key}
                          checked={value}
                          onCheckedChange={(checked) =>
                            handleSettingChange("automation", key, checked)
                          }
                        />
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Integrations */}
        <TabsContent value="integrations" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Integrations</CardTitle>
              <CardDescription>
                Connect external tools and services to enhance your workflow
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {integrations.map((integration) => (
                  <Card key={integration.id}>
                    <CardContent className="p-4">
                      <div className="flex items-start justify-between mb-3">
                        <div className="flex-1">
                          <h3 className="font-medium">{integration.name}</h3>
                          <p className="text-sm text-muted-foreground mt-1">
                            {integration.description}
                          </p>
                        </div>
                        <Badge
                          variant={
                            integration.connected ? "default" : "outline"
                          }
                        >
                          {integration.connected ? "Connected" : "Available"}
                        </Badge>
                      </div>

                      {integration.connected && integration.lastSync && (
                        <p className="text-xs text-muted-foreground mb-3">
                          Last sync: {integration.lastSync}
                        </p>
                      )}

                      <Button
                        variant={integration.connected ? "outline" : "default"}
                        size="sm"
                        className="w-full"
                      >
                        {integration.connected ? "Configure" : "Connect"}
                      </Button>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Notifications */}
        <TabsContent value="notifications" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Notification Preferences</CardTitle>
              <CardDescription>
                Configure how and when you receive notifications
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                {Object.entries(settings.notifications).map(([key, value]) => (
                  <div key={key} className="flex items-center justify-between">
                    <div>
                      <Label htmlFor={key} className="text-sm font-medium">
                        {key
                          .replace(/([A-Z])/g, " $1")
                          .replace(/^./, (str) => str.toUpperCase())}
                      </Label>
                      <p className="text-xs text-muted-foreground">
                        {key === "emailUpdates" &&
                          "Receive email notifications for project updates"}
                        {key === "slackIntegration" &&
                          "Send notifications to connected Slack channels"}
                        {key === "taskAssignments" &&
                          "Get notified when tasks are assigned to you"}
                        {key === "deadlineReminders" &&
                          "Receive reminders before task deadlines"}
                      </p>
                    </div>
                    <Switch
                      id={key}
                      checked={value}
                      onCheckedChange={(checked) =>
                        handleSettingChange("notifications", key, checked)
                      }
                    />
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* Security */}
        <TabsContent value="security" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Access Control</CardTitle>
              <CardDescription>
                Manage project visibility and access permissions
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                {Object.entries(settings.privacy).map(([key, value]) => (
                  <div key={key} className="flex items-center justify-between">
                    <div>
                      <Label htmlFor={key} className="text-sm font-medium">
                        {key
                          .replace(/([A-Z])/g, " $1")
                          .replace(/^./, (str) => str.toUpperCase())}
                      </Label>
                      <p className="text-xs text-muted-foreground">
                        {key === "isPublic" &&
                          "Make this project visible to everyone in the organization"}
                        {key === "allowGuestAccess" &&
                          "Allow external users to view project content"}
                        {key === "requireApproval" &&
                          "Require approval for new team member requests"}
                      </p>
                    </div>
                    <Switch
                      id={key}
                      checked={value}
                      onCheckedChange={(checked) =>
                        handleSettingChange("privacy", key, checked)
                      }
                    />
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card className="border-destructive/50">
            <CardHeader>
              <CardTitle className="text-destructive">Danger Zone</CardTitle>
              <CardDescription>
                Irreversible actions that affect your project
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 border border-destructive/20 rounded-lg">
                  <div>
                    <h4 className="font-medium text-destructive">
                      Delete Project
                    </h4>
                    <p className="text-sm text-muted-foreground">
                      Permanently delete this project and all its data
                    </p>
                  </div>
                  <Button variant="destructive" size="sm">
                    <Trash2 className="w-4 h-4 mr-2" />
                    Delete
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
