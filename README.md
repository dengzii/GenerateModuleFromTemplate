# Generate Module From Template

[![JetBrains IntelliJ plugins](https://img.shields.io/jetbrains/plugin/d/13586-generate-module-from-template) ](https://plugins.jetbrains.com/plugin/13586-generate-module-from-template)
[![JetBrains IntelliJ plugins](https://img.shields.io/jetbrains/plugin/v/13586-generate-module-from-template) ](https://plugins.jetbrains.com/plugin/13586-generate-module-from-template)

[中文 - README](https://github.com/dengzii/GenerateModuleFromTemplate/blob/master/README-ZH.md)

[Video Tutorial - YouTube](https://youtu.be/TyeXnbCcBP4)

### Create a directory structure from a highly customizable template

Using this plugin, help you create directories and files from the customizable template.

### Feature

1. Custom directory structure.
2. Support placeholders, and replace it when you create a module.
3. Specify file templates from IDE custom/build-in templates.
4. Passing placeholders to file template as variables.
5. Output/import template file. share your template with your partner.

### Usage

1. Configure template in plugin settings: <b>File > Settings > Tools > Module Template Settings</b>.
2. Create directories from the 'Structure' tab, click the right mouse button to operate the file tree.
3. FileTree can use placeholders, the placeholder should like this -> <b>${YOUR_PLACEHOLDER_HERE}</b>.
4. The 'File Template' tab lists which template the specified file uses, you can also use placeholders for FileName
   field.
5. The 'Placeholder' tab's table defines placeholders for replacing filenames and file templates

*NOTE*

- The nested placeholder in dir tree will be calculated and merged to a new placeholder, eg: `${${A}_${B}}`, A=a,
  B=b,result=`${a_b}`.
- The existing files will be skipped.
- The Java class file name may depend on ClassName, you better keep the class name and file name consistent, else the
  file name in the template will not effective.
- The placeholders are best not the same as the built-in property of `Apache Velocity`.

### Build

#### Dependencies

- JDK 8+
- Kotlin
- IntelliJ IDEA (Community Edition) 19.1+

#### Import Project

This project is not a gradle or pom project, but a **IntelliJ Platform Plugin** project, IDEA cannot import this project
normally.

In **IDEA Community** (
necessary) `File -> New -> Project from Exsiting Sources -> Create project  from exsiting sources`, then click next
until finish import.

Pressing the `Ctrl + Alt + Shift + S` to open **Project Structure** dialog, choose **Project** tab, change **Project
SDK** to `IntelliJ IDEA Community Edition IC-xxxx`, then apply change.

Then, edit `GenerateModuleFromTemplate.iml` in the project root directory, change the `type` attribute of `module` node
to `PLUGIN_MODULE`, minimize the IDEA and restore it, the plugin project will be detected.

Finally, `Run -> Edit Configuretions -> Alt + Insert -> Plugin -> Apply`, the project configuration completed.

#### Generate Plugin Jar

`Run -> Prepare Plugin Module xxx For Deployment`

### Changelog

- 1.5.0: Fix: specify a template doesn't work., feature: fetch template variables as placeholders when create the file,
  support specify file template when create the module. ui looks more comfortable.
- 1.4.0: feature: Support export and import template to file, adjust action button position.
- 1.3.1: fix: AucFrame module template bugs.
- 1.3.0: fix: Placeholder don't work when call FileTreeNode.include.
- 1.2.0: feature: all IntelliJ platform IDEs support, file template selection support when edit module template.
- 1.1.0: feature: support create module template, placeholder, file template
- 1.1.0: feature: support create module template, placeholder, file template 1.0: basically feature, generate module
  directories from template
- 1.0.0: basically feature, generate module directories from template

### Screenshot

<img src="https://raw.githubusercontent.com/dengzii/GenerateModuleFromTemplate/master/screenshot/main.png" height="360">
<img src="https://raw.githubusercontent.com/dengzii/GenerateModuleFromTemplate/master/screenshot/preview.png" height="360">
<img src="https://raw.githubusercontent.com/dengzii/GenerateModuleFromTemplate/master/screenshot/settings.png" height="360">
