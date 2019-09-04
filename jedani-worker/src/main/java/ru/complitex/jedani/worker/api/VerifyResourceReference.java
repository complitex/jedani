package ru.complitex.jedani.worker.api;

import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import ru.complitex.jedani.worker.service.WorkerService;

/**
 * @author Anatoly A. Ivanov
 * 04.09.2019 16:58
 */
public class VerifyResourceReference extends ResourceReference {
    public static final VerifyResourceReference INSTANCE = new VerifyResourceReference();

    public VerifyResourceReference() {
        super("VerifyResourceReference");
    }

    @Override
    public IResource getResource() {
        return new AbstractResource() {
            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                ResourceResponse resourceResponse = new ResourceResponse();

                resourceResponse.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(Attributes attributes) {
                        String jId = attributes.getParameters().get("jId").toString();

                        attributes.getResponse().write("{j_id: \"" + jId + "\"," +
                                " is_exist: " + (isExistJId(jId) ? "1" : "0") + "}");
                    }
                });

                return resourceResponse;
            }
        };
    }

    private boolean isExistJId(String jId){
        return getWorkerService().isExistJId(jId);
    }

    private WorkerService workerService;

    private WorkerService getWorkerService(){
        if (workerService == null){
            workerService = new WorkerService();
            NonContextual.of(WorkerService.class).inject(workerService);
        }

        return workerService;
    }
}
