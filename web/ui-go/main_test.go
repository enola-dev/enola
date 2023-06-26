package main

import (
	"context"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	enolagrpc "dev/enola/grpc"
)

func TestHandler(t *testing.T) {
	h := handler{client: enolaClientStub{}}

	entityID := "foo"
	req, err := http.NewRequest(http.MethodGet, "/ui/entity/"+entityID, nil)
	if err != nil {
		t.Fatal(err)
	}

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(h.entity)
	handler.ServeHTTP(rr, req)
	if rr.Code != http.StatusOK {
		t.Errorf("expected status code %d, got %d", http.StatusOK, rr.Code)
	}
	body := rr.Body.String()
	if !strings.Contains(body, entityID) {
		t.Errorf("expected body to contain %s, got %s", entityID, body)
	}
}

type enolaClientStub struct{}

func (e enolaClientStub) getEntity(_ context.Context, id string) (*enolagrpc.Entity, error) {
	return &enolagrpc.Entity{
		Id: &enolagrpc.ID{Entity: id},
	}, nil
}

func (e enolaClientStub) close() error {
	return nil
}
