package org.eclipse.emf.ecp.emf2web.json.generator.seed;

import com.google.common.base.Objects;
import org.eclipse.emf.ecp.emf2web.controller.GenerationInfo;
import org.eclipse.emf.ecp.emf2web.exporter.SchemaWrapper;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class SeedWrapper implements SchemaWrapper {
  @Override
  public String getName() {
    return "JavaScript Example";
  }
  
  @Override
  public String getFileExtension() {
    return "js";
  }
  
  @Override
  public String wrap(final String toWrap, final String type) {
    String _switchResult = null;
    boolean _matched = false;
    if (!_matched) {
      if (Objects.equal(type, GenerationInfo.MODEL_TYPE)) {
        _matched=true;
        CharSequence _wrapModel = this.wrapModel(toWrap);
        _switchResult = _wrapModel.toString();
      }
    }
    if (!_matched) {
      if (Objects.equal(type, GenerationInfo.VIEW_TYPE)) {
        _matched=true;
        CharSequence _wrapView = this.wrapView(toWrap);
        _switchResult = _wrapView.toString();
      }
    }
    if (!_matched) {
      throw new IllegalArgumentException(("Could not wrap: " + type));
    }
    return _switchResult;
  }
  
  public CharSequence wrapModel(final String model) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\'use strict\';");
    _builder.newLine();
    _builder.newLine();
    _builder.append("var app = angular.module(\'jsonforms-seed\');");
    _builder.newLine();
    _builder.append("app.factory(\'SchemaService\', function() {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("schema: ");
    _builder.append(model, "        ");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("});");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence wrapView(final String view) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\'use strict\';");
    _builder.newLine();
    _builder.newLine();
    _builder.append("var app = angular.module(\'jsonforms-seed\');");
    _builder.newLine();
    _builder.append("app.service(\'UISchemaService\', function() {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("this.uiSchema = ");
    _builder.append(view, "    ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("});");
    _builder.newLine();
    return _builder;
  }
}
