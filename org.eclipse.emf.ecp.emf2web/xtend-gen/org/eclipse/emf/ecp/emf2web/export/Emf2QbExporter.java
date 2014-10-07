package org.eclipse.emf.ecp.emf2web.export;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecp.emf2web.export.ClassMapping;
import org.eclipse.emf.ecp.view.spi.group.model.VGroup;
import org.eclipse.emf.ecp.view.spi.horizontal.model.VHorizontalLayout;
import org.eclipse.emf.ecp.view.spi.label.model.VLabel;
import org.eclipse.emf.ecp.view.spi.model.VContainedElement;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VDomainModelReference;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.ecp.view.spi.vertical.model.VVerticalLayout;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class Emf2QbExporter {
  private ClassMapping classMapper = null;
  
  public void export(final Resource ecoreModel, final Set<EClass> selectedClasses, final Set<Resource> viewModels, final File destinationDir) {
    try {
      ClassMapping _classMapping = new ClassMapping();
      this.classMapper = _classMapping;
      final ArrayList<EEnum> allEEnums = new ArrayList<EEnum>();
      TreeIterator<EObject> _allContents = ecoreModel.getAllContents();
      Iterator<EPackage> _filter = Iterators.<EPackage>filter(_allContents, EPackage.class);
      final Procedure1<EPackage> _function = new Procedure1<EPackage>() {
        public void apply(final EPackage ePackage) {
          EList<EClassifier> _eClassifiers = ePackage.getEClassifiers();
          Iterable<EEnum> _filter = Iterables.<EEnum>filter(_eClassifiers, EEnum.class);
          Iterables.<EEnum>addAll(allEEnums, _filter);
        }
      };
      IteratorExtensions.<EPackage>forEach(_filter, _function);
      this.classMapper.addAllEEnum(allEEnums);
      final HashSet<EClass> eClasses = new HashSet<EClass>();
      final HashSet<Resource> vModels = new HashSet<Resource>();
      boolean _equals = Objects.equal(selectedClasses, null);
      if (_equals) {
        TreeIterator<EObject> _allContents_1 = ecoreModel.getAllContents();
        Iterator<EPackage> _filter_1 = Iterators.<EPackage>filter(_allContents_1, EPackage.class);
        final Procedure1<EPackage> _function_1 = new Procedure1<EPackage>() {
          public void apply(final EPackage ePackage) {
            EList<EClassifier> _eClassifiers = ePackage.getEClassifiers();
            Iterable<EClass> _filter = Iterables.<EClass>filter(_eClassifiers, EClass.class);
            Iterables.<EClass>addAll(eClasses, _filter);
          }
        };
        IteratorExtensions.<EPackage>forEach(_filter_1, _function_1);
      } else {
        eClasses.addAll(selectedClasses);
      }
      boolean _notEquals = (!Objects.equal(viewModels, null));
      if (_notEquals) {
        vModels.addAll(viewModels);
      }
      final Consumer<EClass> _function_2 = new Consumer<EClass>() {
        public void accept(final EClass eClass) {
          try {
            final String controllerText = Emf2QbExporter.this.buildControllerFile(eClass);
            String _name = eClass.getName();
            String _plus = ("app/controllers/" + _name);
            final String controllerDest = (_plus + "Controller.scala");
            File _file = new File(destinationDir, controllerDest);
            FileUtils.writeStringToFile(_file, controllerText);
            final String schemaText = Emf2QbExporter.this.buildSchemaFile(eClass, viewModels);
            String _name_1 = eClass.getName();
            String _plus_1 = ("app/controllers/" + _name_1);
            final String schemaDest = (_plus_1 + "Schema.scala");
            File _file_1 = new File(destinationDir, schemaDest);
            FileUtils.writeStringToFile(_file_1, schemaText);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
      };
      eClasses.forEach(_function_2);
      final String routesText = this.buildRoutesFile(eClasses);
      final String routesDest = "conf/routes";
      File _file = new File(destinationDir, routesDest);
      FileUtils.writeStringToFile(_file, routesText);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private String buildRoutesFile(final Set<EClass> selectedClasses) {
    String _xblockexpression = null;
    {
      final ArrayList<EClass> classes = new ArrayList<EClass>(selectedClasses);
      classes.sort(new Comparator<EClass>() {
        public boolean equals(final Object arg0) {
          return Objects.equal(arg0, this);
        }
        
        public int compare(final EClass arg0, final EClass arg1) {
          String _name = arg0.getName();
          String _name_1 = arg1.getName();
          return _name.compareTo(_name_1);
        }
      });
      String _routesIntro = this.routesIntro();
      StringConcatenation _builder = new StringConcatenation();
      {
        for(final EClass eClass : selectedClasses) {
          _builder.append("GET     /");
          String _name = eClass.getName();
          String _lowerCase = _name.toLowerCase();
          _builder.append(_lowerCase, "");
          _builder.append("/model\t\tcontrollers.");
          String _name_1 = eClass.getName();
          _builder.append(_name_1, "");
          _builder.append("Controller.getModel");
          _builder.newLineIfNotEmpty();
          _builder.append("GET     /");
          String _name_2 = eClass.getName();
          String _lowerCase_1 = _name_2.toLowerCase();
          _builder.append(_lowerCase_1, "");
          _builder.append("/view\t\t\tcontrollers.");
          String _name_3 = eClass.getName();
          _builder.append(_name_3, "");
          _builder.append("Controller.getView");
          _builder.newLineIfNotEmpty();
          _builder.append("->\t\t/");
          String _name_4 = eClass.getName();
          String _lowerCase_2 = _name_4.toLowerCase();
          _builder.append(_lowerCase_2, "");
          _builder.append("\t\t\t\tcontrollers.");
          String _name_5 = eClass.getName();
          _builder.append(_name_5, "");
          _builder.append("Router");
          _builder.newLineIfNotEmpty();
          _builder.newLine();
        }
      }
      _xblockexpression = (_routesIntro + _builder);
    }
    return _xblockexpression;
  }
  
  private String routesIntro() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("# Routes");
    _builder.newLine();
    _builder.append("# This file defines all application routes (Higher priority routes first)");
    _builder.newLine();
    _builder.append("# ~~~~");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# Home page");
    _builder.newLine();
    _builder.append("GET     /                           controllers.Application.index");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# Map static resources from the /public folder to the /assets URL path");
    _builder.newLine();
    _builder.append("GET     /assets/*file               controllers.Assets.at(path=\"/public\", file)");
    _builder.newLine();
    _builder.newLine();
    return _builder.toString();
  }
  
  private String buildControllerFile(final EClass eClass) {
    String _xblockexpression = null;
    {
      final String name = eClass.getName();
      StringConcatenation _builder = new StringConcatenation();
      String _controllerIntro = this.controllerIntro();
      _builder.append(_controllerIntro, "");
      _builder.newLineIfNotEmpty();
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("object ");
      _builder_1.append(name, "");
      _builder_1.append("Controller extends MongoController with QBCrudController {");
      _builder_1.newLineIfNotEmpty();
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("lazy val collection = new QBMongoCollection(\"");
      String _lowerCase = name.toLowerCase();
      _builder_1.append(_lowerCase, "  ");
      _builder_1.append("\")(db) with QBCollectionValidation {");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("    ");
      _builder_1.append("override def schema = ");
      _builder_1.append(name, "    ");
      _builder_1.append("Schema.modelSchema");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("  ");
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("override def createSchema = ");
      _builder_1.append(name, "  ");
      _builder_1.append("Schema.modelSchema -- \"id\"");
      _builder_1.newLineIfNotEmpty();
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("def getView = JsonHeaders {");
      _builder_1.newLine();
      _builder_1.append("    ");
      _builder_1.append("Action {");
      _builder_1.newLine();
      _builder_1.append("      ");
      _builder_1.append("Ok(Json.toJson(");
      _builder_1.append(name, "      ");
      _builder_1.append("Schema.viewSchema))");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("    ");
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("def getModel = JsonHeaders {");
      _builder_1.newLine();
      _builder_1.append("    ");
      _builder_1.append("Action {");
      _builder_1.newLine();
      _builder_1.append("      ");
      _builder_1.append("Ok(Json.toJson(");
      _builder_1.append(name, "      ");
      _builder_1.append("Schema.modelSchema))");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("    ");
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.append("  ");
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.append("}");
      _builder_1.newLine();
      _builder_1.newLine();
      _builder_1.append("object ");
      _builder_1.append(name, "");
      _builder_1.append("Router extends QBRouter {");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("  ");
      _builder_1.append("override def qbRoutes = ");
      _builder_1.append(name, "  ");
      _builder_1.append("Controller.crudRoutes");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("}");
      _builder_1.newLine();
      _xblockexpression = (_builder.toString() + _builder_1);
    }
    return _xblockexpression;
  }
  
  private String controllerIntro() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package controllers");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import play.api.libs.concurrent.Execution.Implicits.defaultContext");
    _builder.newLine();
    _builder.append("import play.api.mvc.Action");
    _builder.newLine();
    _builder.append("import play.modules.reactivemongo.MongoController");
    _builder.newLine();
    _builder.append("import org.qbproject.api.schema.QBSchema._");
    _builder.newLine();
    _builder.append("import org.qbproject.api.controllers.{JsonHeaders, QBCrudController}");
    _builder.newLine();
    _builder.append("import org.qbproject.api.mongo.{QBCollectionValidation, QBMongoCollection}");
    _builder.newLine();
    _builder.append("import org.qbproject.api.routing.QBRouter");
    _builder.newLine();
    _builder.append("import play.api.libs.json.{JsUndefined, JsValue, Json}");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String buildSchemaFile(final EClass eClass, final Set<Resource> viewModels) {
    StringConcatenation _builder = new StringConcatenation();
    String _scalaIntro = this.scalaIntro();
    _builder.append(_scalaIntro, "");
    _builder.newLineIfNotEmpty();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("object ");
    String _name = eClass.getName();
    _builder_1.append(_name, "");
    _builder_1.append("Schema {");
    _builder_1.newLineIfNotEmpty();
    _builder_1.append("\t\t");
    String _buildModelObject = this.buildModelObject(eClass);
    _builder_1.append(_buildModelObject, "\t\t");
    _builder_1.newLineIfNotEmpty();
    _builder_1.append("\t\t");
    String _buildViewModelObject = this.buildViewModelObject(eClass, viewModels);
    _builder_1.append(_buildViewModelObject, "\t\t");
    _builder_1.newLineIfNotEmpty();
    _builder_1.append("}");
    _builder_1.newLine();
    return (_builder.toString() + _builder_1);
  }
  
  private String buildViewModelObject(final EClass eClass, final Set<Resource> viewModels) {
    String _xblockexpression = null;
    {
      final Resource viewModel = this.findViewModel(eClass, viewModels);
      String _xifexpression = null;
      boolean _equals = Objects.equal(viewModel, null);
      if (_equals) {
        _xifexpression = this.buildDefaultViewModel(eClass);
      } else {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("val viewSchema = QBViewModel(");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("modelSchema,");
        _builder.newLine();
        _builder.append("\t");
        EList<EObject> _contents = viewModel.getContents();
        EObject _get = _contents.get(0);
        String _buildViewModel = this.buildViewModel(eClass, _get);
        _builder.append(_buildViewModel, "\t");
        _builder.newLineIfNotEmpty();
        _builder.append(")");
        _builder.newLine();
        _xifexpression = _builder.toString();
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  private String buildViewModel(final EClass eClass, final EObject viewModelElement) {
    String _switchResult = null;
    boolean _matched = false;
    if (!_matched) {
      if (viewModelElement instanceof VView) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        {
          EList<VContainedElement> _children = ((VView)viewModelElement).getChildren();
          boolean _hasElements = false;
          for(final VContainedElement element : _children) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "");
            }
            String _buildViewModel = this.buildViewModel(eClass, element);
            _builder.append(_buildViewModel, "");
            _builder.newLineIfNotEmpty();
          }
        }
        _switchResult = _builder.toString();
      }
    }
    if (!_matched) {
      if (viewModelElement instanceof VHorizontalLayout) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("QBHorizontalLayout(");
        _builder.newLine();
        {
          EList<VContainedElement> _children = ((VHorizontalLayout)viewModelElement).getChildren();
          boolean _hasElements = false;
          for(final VContainedElement element : _children) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "\t");
            }
            _builder.append("\t");
            String _buildViewModel = this.buildViewModel(eClass, element);
            _builder.append(_buildViewModel, "\t");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append(")");
        _builder.newLine();
        _switchResult = _builder.toString();
      }
    }
    if (!_matched) {
      if (viewModelElement instanceof VVerticalLayout) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("QBVerticalLayout(");
        _builder.newLine();
        {
          EList<VContainedElement> _children = ((VVerticalLayout)viewModelElement).getChildren();
          boolean _hasElements = false;
          for(final VContainedElement element : _children) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "\t");
            }
            _builder.append("\t");
            String _buildViewModel = this.buildViewModel(eClass, element);
            _builder.append(_buildViewModel, "\t");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append(")");
        _builder.newLine();
        _switchResult = _builder.toString();
      }
    }
    if (!_matched) {
      if (viewModelElement instanceof VGroup) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("QBGroup(\"");
        String _name = ((VGroup)viewModelElement).getName();
        _builder.append(_name, "");
        _builder.append("\",");
        _builder.newLineIfNotEmpty();
        {
          EList<VContainedElement> _children = ((VGroup)viewModelElement).getChildren();
          boolean _hasElements = false;
          for(final VContainedElement element : _children) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(",", "\t");
            }
            _builder.append("\t");
            String _buildViewModel = this.buildViewModel(eClass, element);
            _builder.append(_buildViewModel, "\t");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append(")");
        _builder.newLine();
        _switchResult = _builder.toString();
      }
    }
    if (!_matched) {
      if (viewModelElement instanceof VLabel) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("QBLabel(\"");
        String _name = ((VLabel)viewModelElement).getName();
        _builder.append(_name, "");
        _builder.append("\")");
        _builder.newLineIfNotEmpty();
        _switchResult = _builder.toString();
      }
    }
    if (!_matched) {
      if (viewModelElement instanceof VControl) {
        _matched=true;
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("QBViewControl(\"");
        VDomainModelReference _domainModelReference = ((VControl)viewModelElement).getDomainModelReference();
        Iterator<EStructuralFeature> _eStructuralFeatureIterator = _domainModelReference.getEStructuralFeatureIterator();
        EStructuralFeature _next = _eStructuralFeatureIterator.next();
        String _name = _next.getName();
        _builder.append(_name, "");
        _builder.append("\", QBViewPath(\"");
        VDomainModelReference _domainModelReference_1 = ((VControl)viewModelElement).getDomainModelReference();
        Iterator<EStructuralFeature> _eStructuralFeatureIterator_1 = _domainModelReference_1.getEStructuralFeatureIterator();
        EStructuralFeature _next_1 = _eStructuralFeatureIterator_1.next();
        String _name_1 = _next_1.getName();
        _builder.append(_name_1, "");
        _builder.append("\"))");
        _builder.newLineIfNotEmpty();
        _switchResult = _builder.toString();
      }
    }
    if (!_matched) {
      _switchResult = "";
    }
    return _switchResult;
  }
  
  private Resource findViewModel(final EClass eClass, final Set<Resource> viewModels) {
    final Function1<Resource, Boolean> _function = new Function1<Resource, Boolean>() {
      public Boolean apply(final Resource viewResource) {
        EList<EObject> _contents = viewResource.getContents();
        final EObject root = _contents.get(0);
        if ((root instanceof VView)) {
          EClass _rootEClass = ((VView)root).getRootEClass();
          return Boolean.valueOf(Objects.equal(_rootEClass, eClass));
        }
        return Boolean.valueOf(false);
      }
    };
    return IterableExtensions.<Resource>findFirst(viewModels, _function);
  }
  
  private String scalaIntro() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package controllers");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import org.qbproject.api.schema.QBSchema._");
    _builder.newLine();
    _builder.append("import org.qbproject.api.mongo.MongoSchemaExtensions._");
    _builder.newLine();
    _builder.append("import controllers.QBView._");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String buildModelObject(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("val modelSchema = qbClass(\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("\"id\" -> objectId,");
    _builder.newLine();
    {
      EList<EStructuralFeature> _eAllStructuralFeatures = eClass.getEAllStructuralFeatures();
      final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
        public Boolean apply(final EStructuralFeature f) {
          EClassifier _eType = f.getEType();
          return Boolean.valueOf(Emf2QbExporter.this.classMapper.isAllowed(_eType));
        }
      };
      Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(_eAllStructuralFeatures, _function);
      boolean _hasElements = false;
      for(final EStructuralFeature eStructuralFeature : _filter) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "\t");
        }
        _builder.append("\t");
        _builder.append("\"");
        String _name = eStructuralFeature.getName();
        _builder.append(_name, "\t");
        _builder.append("\" -> ");
        EClassifier _eType = eStructuralFeature.getEType();
        String _qBName = this.classMapper.getQBName(_eType);
        _builder.append(_qBName, "\t");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append(")");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String buildDefaultViewModel(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("val viewSchema = QBViewModel(\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("modelSchema,");
    _builder.newLine();
    {
      EList<EStructuralFeature> _eAllStructuralFeatures = eClass.getEAllStructuralFeatures();
      final Function1<EStructuralFeature, Boolean> _function = new Function1<EStructuralFeature, Boolean>() {
        public Boolean apply(final EStructuralFeature f) {
          EClassifier _eType = f.getEType();
          return Boolean.valueOf(Emf2QbExporter.this.classMapper.isAllowed(_eType));
        }
      };
      Iterable<EStructuralFeature> _filter = IterableExtensions.<EStructuralFeature>filter(_eAllStructuralFeatures, _function);
      boolean _hasElements = false;
      for(final EStructuralFeature eStructuralFeature : _filter) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "\t");
        }
        _builder.append("\t");
        _builder.append("QBViewControl(\"");
        String _name = eStructuralFeature.getName();
        _builder.append(_name, "\t");
        _builder.append("\", QBViewPath(\"");
        String _name_1 = eStructuralFeature.getName();
        _builder.append(_name_1, "\t");
        _builder.append("\"))");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append(")");
    _builder.newLine();
    return _builder.toString();
  }
}
