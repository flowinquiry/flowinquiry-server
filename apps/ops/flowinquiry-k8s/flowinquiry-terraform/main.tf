resource "helm_release" "flowinquiry" {
  name             = "flowinquiry"
  chart            = "${path.module}/../flowinquiry-helm"
  namespace        = "flowinquiry"
  create_namespace = true

  values = [
    file("${path.module}/values/values.yaml")
  ]
}
