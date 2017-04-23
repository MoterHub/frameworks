package org.unidal.lookup.container.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.container.ComponentKey;
import org.unidal.lookup.container.ComponentManager;
import org.unidal.lookup.container.model.entity.ComponentModel;

public class ComponentLifecycle {
   private List<LifecycleHandler> m_handlers = new ArrayList<LifecycleHandler>();

   private ComponentManager m_manager;

   public ComponentLifecycle(ComponentManager manager) {
      m_manager = manager;
   }

   public void addHandle(LifecycleHandler handler) {
      m_handlers.add(handler);
   }

   public void start(Object component, ComponentModel model) throws ComponentLookupException {
      LifecycleContext ctx = new ComponentContext(component).setComponentModel(model);

      for (LifecycleHandler handler : m_handlers) {
         handler.handleStart(ctx);
      }
   }

   public void stop(Object component) {
      LifecycleContext ctx = new ComponentContext(component);

      for (LifecycleHandler handler : m_handlers) {
         handler.handleStop(ctx);
      }
   }

   private class ComponentContext implements LifecycleContext {
      private Object m_component;

      private ComponentModel m_model;

      public ComponentContext(Object component) {
         m_component = component;
      }

      @Override
      public Object getComponent() {
         return m_component;
      }

      @Override
      public ComponentModel getComponentModel() {
         return m_model;
      }

      @Override
      public Logger getLogger(String role) {
         return m_manager.getLoggerManager().getLoggerForComponent(role);
      }

      @Override
      public Object lookup(String role, String roleHint) throws ComponentLookupException {
         return m_manager.lookup(new ComponentKey(role, roleHint));
      }

      public ComponentContext setComponentModel(ComponentModel model) {
         m_model = model;
         return this;
      }

      @Override
      public PlexusContainer getContainer() {
         return m_manager.getContainer();
      }
   }
}
