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

    private WorkerService workerService;

    public VerifyResourceReference() {
        super("VerifyResourceReference");
    }

    private WorkerService getWorkerService(){
        if (workerService == null){
            workerService = new WorkerService();
            NonContextual.of(WorkerService.class).inject(workerService);
        }

        return workerService;
    }

    private boolean isExistJId(String jId){
        return getWorkerService().isExistJId(jId);
    }

    @Override
    public IResource getResource() {
        return new AbstractResource() {
            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                ResourceResponse resourceResponse = new ResourceResponse();

                resourceResponse.setContentType("application/json");
                resourceResponse.setTextEncoding("utf-8");

                resourceResponse.setWriteCallback(new WriteCallback() {
                    @Override
                    public void writeData(Attributes attributes) {
                        String jId = attributes.getParameters().get("jId").toString();

                        attributes.getResponse().write("{\"j_id\": \"" + jId + "\"," +
                                " \"is_exist\": " + (isExistJId(jId) ? "1" : "0") + "}");
                    }
                });

                return resourceResponse;
            }
        };
    }


}
